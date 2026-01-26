package com.careermappro.services;

import com.careermappro.entities.Proficiency;
import com.careermappro.repositories.ProficiencyRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    private final ProficiencyRepository proficiencyRepo;

    public AnalyticsService(ProficiencyRepository proficiencyRepo) {
        this.proficiencyRepo = proficiencyRepo;
    }

    /**
     * Calculate overall career readiness for a user (0-10 scale)
     * Based on average proficiency across all skills
     */
    public double getOverallReadiness(Integer userId) {
        List<Proficiency> skills = proficiencyRepo.findByUserId(userId);

        if (skills.isEmpty()) {
            return 0.0;
        }

        double sum = skills.stream()
                .mapToDouble(Proficiency::getProficiency)
                .sum();

        return sum / skills.size();
    }

    /**
     * Get average readiness scores grouped by domain
     * Domains are inferred from skill names (e.g., "Java" -> "Backend", "React" -> "Frontend")
     */
    public Map<String, Double> getDomainAverages(Integer userId) {
        List<Proficiency> skills = proficiencyRepo.findByUserId(userId);

        if (skills.isEmpty()) {
            return new HashMap<>();
        }

        // Group skills by domain
        Map<String, List<Double>> domainSkills = new HashMap<>();

        for (Proficiency skill : skills) {
            String domain = inferDomain(skill.getSkill());
            domainSkills.computeIfAbsent(domain, k -> new ArrayList<>())
                    .add(skill.getProficiency());
        }

        // Calculate average for each domain
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : domainSkills.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            averages.put(entry.getKey(), avg);
        }

        return averages;
    }

    /**
     * Convert numeric readiness to human-readable label
     */
    public String getReadinessLabel(double readiness) {
        if (readiness < 2.0) return "Beginner";
        if (readiness < 4.0) return "Learning";
        if (readiness < 6.0) return "Developing";
        if (readiness < 8.0) return "Proficient";
        if (readiness < 9.0) return "Advanced";
        return "Expert";
    }

    /**
     * Calculate readiness for a specific career goal
     * Based on matching user's skills with required skills for that goal
     */
    public double calculateGoalReadiness(Integer userId, String goalDescription) {
        List<Proficiency> userSkills = proficiencyRepo.findByUserId(userId);

        if (userSkills.isEmpty()) {
            return 0.0;
        }

        // Extract key skills mentioned in the goal
        Set<String> relevantSkills = extractRelevantSkills(goalDescription, userSkills);

        if (relevantSkills.isEmpty()) {
            // If no specific skills match, use overall readiness
            return getOverallReadiness(userId);
        }

        // Calculate average proficiency for relevant skills
        double sum = userSkills.stream()
                .filter(p -> relevantSkills.contains(p.getSkill().toLowerCase()))
                .mapToDouble(Proficiency::getProficiency)
                .sum();

        return sum / relevantSkills.size();
    }

    /**
     * Infer domain from skill name
     * This is a simple heuristic - can be improved with ML or manual mapping
     */
    private String inferDomain(String skillName) {
        String lower = skillName.toLowerCase();

        // Backend technologies
        if (lower.matches(".*(java|spring|python|django|node|express|api|sql|database|postgres|mysql).*")) {
            return "Backend";
        }

        // Frontend technologies
        if (lower.matches(".*(react|vue|angular|javascript|typescript|html|css|tailwind|next).*")) {
            return "Frontend";
        }

        // DevOps/Infrastructure
        if (lower.matches(".*(docker|kubernetes|aws|azure|gcp|terraform|jenkins|ci/cd|devops).*")) {
            return "DevOps";
        }

        // Data Science/ML
        if (lower.matches(".*(machine learning|ml|ai|data science|tensorflow|pytorch|pandas|numpy).*")) {
            return "Data Science";
        }

        // Mobile
        if (lower.matches(".*(android|ios|swift|kotlin|react native|flutter).*")) {
            return "Mobile";
        }

        // Default to "General" if no match
        return "General";
    }

    /**
     * Extract skills from goal description that match user's existing skills
     */
    private Set<String> extractRelevantSkills(String goalDescription, List<Proficiency> userSkills) {
        String lowerGoal = goalDescription.toLowerCase();
        Set<String> relevant = new HashSet<>();

        for (Proficiency skill : userSkills) {
            String lowerSkill = skill.getSkill().toLowerCase();
            if (lowerGoal.contains(lowerSkill)) {
                relevant.add(lowerSkill);
            }
        }

        return relevant;
    }
}
