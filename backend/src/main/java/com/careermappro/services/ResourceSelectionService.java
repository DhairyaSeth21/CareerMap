package com.careermappro.services;

import com.careermappro.models.*;
import com.careermappro.repositories.*;
import com.careermappro.entities.SkillNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for selecting and personalizing learning resources
 * Implements intelligent scoring algorithm based on user preferences
 */
@Service
public class ResourceSelectionService {

    @Autowired
    private CuratedResourceRepository curatedResourceRepository;

    @Autowired
    private UserResourceRatingRepository userResourceRatingRepository;

    @Autowired
    private NodeResourceRepository nodeResourceRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    @Autowired
    private OpenAIService openAIService;

    private static final int DEFAULT_RESOURCE_COUNT = 2;
    private static final float USER_RATING_WEIGHT = 0.7f;
    private static final float GLOBAL_RATING_WEIGHT = 0.3f;
    private static final float RECENCY_BOOST = 0.5f;
    private static final float OLD_PENALTY = 0.5f;
    private static final float POPULARITY_BOOST = 0.3f;
    private static final float TYPE_PREFERENCE_BOOST = 0.4f;

    /**
     * Get personalized resources for a node
     * Returns 2-3 resources tailored to user preferences
     */
    @Transactional
    public List<CuratedResource> getResourcesForNode(Integer userId, Integer nodeId) {
        System.out.println("[RESOURCE-SELECT] Getting resources for user=" + userId + ", node=" + nodeId);

        // 1. Get all resources linked to this node
        List<CuratedResource> allResources = curatedResourceRepository.findByNodeId(nodeId);
        System.out.println("[RESOURCE-SELECT] Found " + allResources.size() + " resources for node");

        // 2. Filter out resources user already rated negatively (< 3 stars or marked unhelpful)
        Set<Integer> excludedResourceIds = getExcludedResourceIds(userId);
        List<CuratedResource> candidateResources = allResources.stream()
                .filter(r -> !excludedResourceIds.contains(r.getResourceId()))
                .collect(Collectors.toList());

        System.out.println("[RESOURCE-SELECT] After filtering: " + candidateResources.size() + " candidates");

        // 3. If not enough resources, fetch new ones via OpenAI
        if (candidateResources.size() < DEFAULT_RESOURCE_COUNT) {
            System.out.println("[RESOURCE-SELECT] Not enough resources, fetching new ones via OpenAI");
            List<CuratedResource> newResources = discoverNewResources(nodeId);
            candidateResources.addAll(newResources);
        }

        // 4. Calculate personalized scores for each resource
        for (CuratedResource resource : candidateResources) {
            float personalizedScore = calculatePersonalizedScore(resource, userId);
            resource.setPersonalizedScore(personalizedScore);

            // Also set user's rating if they've rated it
            Optional<UserResourceRating> userRating =
                userResourceRatingRepository.findByUserIdAndResourceId(userId, resource.getResourceId());
            userRating.ifPresent(rating -> resource.setUserRating(rating.getRating()));
        }

        // 5. Sort by personalized score (descending)
        candidateResources.sort((r1, r2) ->
            Float.compare(r2.getPersonalizedScore(), r1.getPersonalizedScore()));

        // 6. Return top 3 resources
        List<CuratedResource> topResources = candidateResources.stream()
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("[RESOURCE-SELECT] Returning " + topResources.size() + " top resources");
        return topResources;
    }

    /**
     * Calculate personalized score for a resource based on user preferences
     * Factors: user rating (70%), global quality (30%), recency, popularity, type preference
     */
    private float calculatePersonalizedScore(CuratedResource resource, Integer userId) {
        float score = resource.getAvgQualityScore(); // Base: 1.0-5.0

        // 1. User's personal rating (if exists) - HIGHEST WEIGHT
        Optional<UserResourceRating> userRating =
            userResourceRatingRepository.findByUserIdAndResourceId(userId, resource.getResourceId());

        if (userRating.isPresent()) {
            if (!userRating.get().getHelpful()) {
                return -1.0f; // User explicitly disliked - exclude
            }
            // Weight user rating 70%, global rating 30%
            score = GLOBAL_RATING_WEIGHT * score + USER_RATING_WEIGHT * userRating.get().getRating();
        }

        // 2. Recency boost (newer resources preferred)
        long daysSinceVerified = Duration.between(
            resource.getLastVerified(),
            LocalDateTime.now()
        ).toDays();

        if (daysSinceVerified < 30) {
            score += RECENCY_BOOST; // Recent = bonus
        } else if (daysSinceVerified > 365) {
            score -= OLD_PENALTY; // Old = penalty
        }

        // 3. Global popularity (total ratings)
        if (resource.getTotalRatings() != null && resource.getTotalRatings() > 100) {
            score += POPULARITY_BOOST; // Well-vetted resources get a boost
        }

        // 4. User preference for resource type (video vs article)
        String preferredType = getUserPreferredResourceType(userId);
        if (preferredType != null && resource.getType().equals(preferredType)) {
            score += TYPE_PREFERENCE_BOOST; // User prefers this type
        }

        return Math.max(0.0f, Math.min(5.0f, score)); // Clamp to 0-5 range
    }

    /**
     * Get resource IDs that should be excluded for this user
     * (rated < 3 stars or marked as unhelpful)
     */
    private Set<Integer> getExcludedResourceIds(Integer userId) {
        List<UserResourceRating> negativeRatings =
            userResourceRatingRepository.findByUserIdAndRatingLessThan(userId, 3.0f);

        List<UserResourceRating> unhelpfulRatings =
            userResourceRatingRepository.findByUserIdAndHelpfulFalse(userId);

        Set<Integer> excludedIds = new HashSet<>();
        negativeRatings.forEach(r -> excludedIds.add(r.getResourceId()));
        unhelpfulRatings.forEach(r -> excludedIds.add(r.getResourceId()));

        return excludedIds;
    }

    /**
     * Get user's preferred resource type based on their high ratings
     * Returns the type (video/article/etc.) they rate highest on average
     */
    private String getUserPreferredResourceType(Integer userId) {
        try {
            Object[] result = userResourceRatingRepository.findPreferredResourceType(userId);
            if (result != null && result.length > 0) {
                return (String) result[0]; // First column is the type
            }
        } catch (Exception e) {
            System.err.println("[RESOURCE-SELECT] Error finding preferred type: " + e.getMessage());
        }
        return null; // No preference found
    }

    /**
     * Discover new resources via OpenAI and add them to the catalog
     */
    @Transactional
    public List<CuratedResource> discoverNewResources(Integer nodeId) {
        System.out.println("[RESOURCE-DISCOVER] Discovering new resources for node=" + nodeId);

        // Get node details
        Optional<SkillNode> nodeOpt = skillNodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            System.err.println("[RESOURCE-DISCOVER] Node not found: " + nodeId);
            return List.of();
        }

        SkillNode node = nodeOpt.get();

        // Build prompt for OpenAI
        String prompt = buildResourceDiscoveryPrompt(node);

        // Call OpenAI to find resources
        List<CuratedResource> newResources = openAIService.discoverLearningResources(prompt, node);

        // Verify URLs and save valid resources
        List<CuratedResource> validResources = new ArrayList<>();
        for (CuratedResource resource : newResources) {
            if (verifyResourceURL(resource.getUrl())) {
                // Save to catalog
                CuratedResource saved = curatedResourceRepository.save(resource);

                // Link to node
                int priority = nodeResourceRepository.getNextPriority(nodeId);
                NodeResource nodeResource = new NodeResource(nodeId, saved.getResourceId(), priority);
                nodeResourceRepository.save(nodeResource);

                validResources.add(saved);
                System.out.println("[RESOURCE-DISCOVER] Added new resource: " + saved.getTitle());
            } else {
                System.err.println("[RESOURCE-DISCOVER] Invalid URL, skipping: " + resource.getUrl());
            }
        }

        System.out.println("[RESOURCE-DISCOVER] Added " + validResources.size() + " new resources");
        return validResources;
    }

    /**
     * Build prompt for OpenAI resource discovery
     */
    private String buildResourceDiscoveryPrompt(SkillNode node) {
        return String.format("""
            Find 3 high-quality learning resources for: %s

            Domain: %s
            Difficulty: %s

            Requirements:
            - Resources must be from reputable sources (official docs, MDN, freeCodeCamp, O'Reilly, YouTube channels like Fireship, Traversy Media, The Net Ninja)
            - URLs must be real and working (no hallucinations)
            - Mix of types: 1 article/documentation, 1 video, 1 interactive/course
            - Up-to-date (2023-2026)
            - Free or freemium resources only
            - Beginner to intermediate level

            Return JSON array:
            [
              {
                "title": "...",
                "url": "...",
                "type": "video",
                "source": "youtube",
                "description": "...",
                "estimatedMinutes": 15
              }
            ]
            """, node.getCanonicalName(), node.getDomain(), node.getDifficulty());
    }

    /**
     * Verify that a resource URL is accessible (HEAD request)
     * Returns true if URL returns 200, 301, or 302
     */
    private boolean verifyResourceURL(String urlString) {
        try {
            URI uri = URI.create(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            return responseCode == 200 || responseCode == 301 || responseCode == 302;
        } catch (Exception e) {
            System.err.println("[RESOURCE-VERIFY] Failed to verify URL: " + urlString + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Save user rating for a resource
     * Updates both user rating and global quality score
     */
    @Transactional
    public void rateResource(Integer userId, Integer resourceId, Float rating, Boolean helpful, String feedback) {
        System.out.println("[RESOURCE-RATE] User " + userId + " rating resource " + resourceId + ": " + rating + " stars");

        // Save or update user rating
        UserResourceRating userRating = new UserResourceRating(userId, resourceId, rating, helpful);
        if (feedback != null && !feedback.isEmpty()) {
            userRating.setFeedback(feedback);
        }
        userResourceRatingRepository.save(userRating);

        // Update global quality score
        updateGlobalQualityScore(resourceId);

        System.out.println("[RESOURCE-RATE] Rating saved successfully");
    }

    /**
     * Update global quality score for a resource based on all user ratings
     */
    private void updateGlobalQualityScore(Integer resourceId) {
        Float avgRating = userResourceRatingRepository.calculateAvgRating(resourceId);
        Integer totalRatings = userResourceRatingRepository.countRatings(resourceId);

        if (avgRating != null && totalRatings != null) {
            Optional<CuratedResource> resourceOpt = curatedResourceRepository.findById(resourceId);
            if (resourceOpt.isPresent()) {
                CuratedResource resource = resourceOpt.get();
                resource.setAvgQualityScore(avgRating);
                resource.setTotalRatings(totalRatings);
                curatedResourceRepository.save(resource);

                System.out.println("[RESOURCE-UPDATE] Updated global score: " + avgRating + " (" + totalRatings + " ratings)");
            }
        }
    }

    /**
     * Find a different resource for user (when they click "Find different")
     * Marks current resource as unhelpful and returns new recommendations
     */
    @Transactional
    public List<CuratedResource> findDifferentResource(Integer userId, Integer nodeId, Integer currentResourceId) {
        System.out.println("[RESOURCE-DIFFERENT] Finding different resource for user=" + userId + ", node=" + nodeId);

        // Mark current resource as unhelpful (rating=1, helpful=false)
        rateResource(userId, currentResourceId, 1.0f, false, "User requested different resource");

        // Get new recommendations (will exclude the resource we just marked unhelpful)
        return getResourcesForNode(userId, nodeId);
    }
}
