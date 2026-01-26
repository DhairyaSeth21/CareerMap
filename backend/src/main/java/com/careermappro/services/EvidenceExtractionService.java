package com.careermappro.services;

import com.careermappro.entities.*;
import com.careermappro.repositories.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvidenceExtractionService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    private final EvidenceRepository evidenceRepo;
    private final EvidenceSkillLinkRepository linkRepo;
    private final SkillNodeRepository skillNodeRepo;
    private final StateTransitionService stateTransition;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public EvidenceExtractionService(
            EvidenceRepository evidenceRepo,
            EvidenceSkillLinkRepository linkRepo,
            SkillNodeRepository skillNodeRepo,
            StateTransitionService stateTransition) {
        this.evidenceRepo = evidenceRepo;
        this.linkRepo = linkRepo;
        this.skillNodeRepo = skillNodeRepo;
        this.stateTransition = stateTransition;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ingest evidence: Extract skills via OpenAI → Normalize → Create links → Trigger state updates
     */
    @Transactional
    public Map<String, Object> ingestEvidence(Integer userId, Evidence.EvidenceType type, String rawText, String sourceUri) {
        Map<String, Object> result = new HashMap<>();

        // 1. Save evidence
        Evidence evidence = new Evidence(userId, type, rawText);
        evidence.setSourceUri(sourceUri);
        evidence = evidenceRepo.save(evidence);
        result.put("evidenceId", evidence.getEvidenceId());

        // 2. Extract skills via OpenAI
        List<ExtractedSkill> extractedSkills = extractSkillsWithOpenAI(rawText, type);
        result.put("extractedSkills", extractedSkills.size());

        // 3. Normalize to canonical skill IDs
        List<SkillNode> allSkills = skillNodeRepo.findAll();
        Map<String, SkillNode> skillMap = buildSkillMap(allSkills);

        List<EvidenceSkillLink> links = new ArrayList<>();
        Set<Integer> updatedSkillIds = new HashSet<>();

        for (ExtractedSkill extracted : extractedSkills) {
            SkillNode canonical = findCanonicalSkill(extracted.getSkillName(), skillMap);
            if (canonical != null) {
                // 4. Create evidence-skill link
                EvidenceSkillLink link = new EvidenceSkillLink(
                    evidence.getEvidenceId(),
                    canonical.getSkillNodeId(),
                    extracted.getSupport(),
                    "gpt-4o-mini"
                );
                link.setConfidence(extracted.getConfidence());
                links.add(linkRepo.save(link));

                // 5. Trigger state transition
                stateTransition.updateStateFromEvidence(
                    userId,
                    canonical.getSkillNodeId(),
                    extracted.getSupport(),
                    type.toString()
                );

                updatedSkillIds.add(canonical.getSkillNodeId());
            }
        }

        // 6. Recompute frontier for affected skills
        for (Integer skillId : updatedSkillIds) {
            stateTransition.recomputeFrontierForSkill(userId, skillId);
        }

        result.put("linksCreated", links.size());
        result.put("skillsUpdated", updatedSkillIds.size());
        result.put("frontierRecomputed", true);

        return result;
    }

    /**
     * Extract skills from raw text using OpenAI with strict JSON schema
     */
    private List<ExtractedSkill> extractSkillsWithOpenAI(String rawText, Evidence.EvidenceType type) {
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            // Fallback: Return empty list if no API key
            return new ArrayList<>();
        }

        try {
            String prompt = buildExtractionPrompt(rawText, type);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a skill extraction expert. Extract technical skills from evidence and assess proficiency level."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.2);
            requestBody.put("response_format", Map.of("type", "json_object"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );

            // Parse response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Parse JSON response
                    Map<String, Object> parsed = objectMapper.readValue(content, Map.class);
                    List<Map<String, Object>> skills = (List<Map<String, Object>>) parsed.get("skills");

                    return skills.stream()
                        .map(s -> new ExtractedSkill(
                            (String) s.get("name"),
                            ((Number) s.get("support")).doubleValue(),
                            ((Number) s.get("confidence")).doubleValue()
                        ))
                        .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("OpenAI extraction failed: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Build extraction prompt based on evidence type
     */
    private String buildExtractionPrompt(String rawText, Evidence.EvidenceType type) {
        String typeContext = switch (type) {
            case QUIZ -> "This is quiz evidence. Extract skills tested and assess proficiency based on score.";
            case PROJECT -> "This is a project description. Extract technical skills demonstrated.";
            case REPO -> "This is a code repository description. Extract programming languages, frameworks, and tools.";
            case CERT -> "This is a certification. Extract validated skills.";
            case WORK_SAMPLE -> "This is a work sample. Extract demonstrated competencies.";
        };

        return String.format("""
            %s

            Text:
            %s

            Extract technical skills and return JSON in this EXACT format:
            {
              "skills": [
                {
                  "name": "JavaScript",
                  "support": 0.85,
                  "confidence": 0.9
                }
              ]
            }

            Rules:
            - support: 0.0-1.0 (how strongly this evidence proves the skill)
            - confidence: 0.0-1.0 (your confidence in extraction accuracy)
            - Only extract specific technical skills (languages, frameworks, tools)
            - Normalize names (e.g., "JS" → "JavaScript", "React.js" → "React")
            """, typeContext, rawText.substring(0, Math.min(rawText.length(), 2000)));
    }

    /**
     * Build skill map with canonical names + aliases
     */
    private Map<String, SkillNode> buildSkillMap(List<SkillNode> skills) {
        Map<String, SkillNode> map = new HashMap<>();

        for (SkillNode skill : skills) {
            // Add canonical name
            map.put(skill.getCanonicalName().toLowerCase(), skill);

            // Add aliases
            if (skill.getAliases() != null && !skill.getAliases().isEmpty()) {
                try {
                    List<String> aliases = objectMapper.readValue(skill.getAliases(), List.class);
                    for (String alias : aliases) {
                        map.put(alias.toLowerCase(), skill);
                    }
                } catch (Exception e) {
                    // Skip malformed aliases
                }
            }
        }

        return map;
    }

    /**
     * Find canonical skill from extracted name (fuzzy matching via aliases)
     */
    private SkillNode findCanonicalSkill(String extractedName, Map<String, SkillNode> skillMap) {
        String normalized = extractedName.toLowerCase().trim();

        // Direct match
        if (skillMap.containsKey(normalized)) {
            return skillMap.get(normalized);
        }

        // Try common variations
        String[] variations = {
            normalized.replace(".", ""),
            normalized.replace("-", ""),
            normalized.replace(" ", ""),
            normalized + ".js"
        };

        for (String variant : variations) {
            if (skillMap.containsKey(variant)) {
                return skillMap.get(variant);
            }
        }

        return null; // Not found - could log for future ontology expansion
    }

    /**
     * DTO for extracted skills from OpenAI
     */
    private static class ExtractedSkill {
        @JsonProperty("name")
        private String skillName;

        @JsonProperty("support")
        private Double support;

        @JsonProperty("confidence")
        private Double confidence;

        public ExtractedSkill() {}

        public ExtractedSkill(String skillName, Double support, Double confidence) {
            this.skillName = skillName;
            this.support = support;
            this.confidence = confidence;
        }

        public String getSkillName() { return skillName; }
        public Double getSupport() { return support; }
        public Double getConfidence() { return confidence; }
    }
}
