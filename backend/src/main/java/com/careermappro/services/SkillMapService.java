package com.careermappro.services;

import com.careermappro.entities.Skill;
import com.careermappro.entities.SkillDependency;
import com.careermappro.entities.UserSkill;
import com.careermappro.repositories.SkillRepository;
import com.careermappro.repositories.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Skill Map Service - Core Graph-Based Skill System
 *
 * This service manages the skill graph:
 * - Skills are nodes with categories and difficulty levels
 * - Dependencies are edges (Spring Boot requires Java, Next.js requires React)
 * - User skills track proficiency (0-100) with evidence
 */
@Service
public class SkillMapService {

    private final SkillRepository skillRepo;
    private final UserSkillRepository userSkillRepo;

    public SkillMapService(SkillRepository skillRepo, UserSkillRepository userSkillRepo) {
        this.skillRepo = skillRepo;
        this.userSkillRepo = userSkillRepo;
    }

    /**
     * Get full skill graph with all dependencies
     */
    public Map<String, Object> getSkillGraph() {
        List<Skill> allSkills = skillRepo.findAll();

        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", allSkills.stream()
            .map(skill -> Map.of(
                "id", skill.getSkillId(),
                "name", skill.getName(),
                "category", skill.getCategory(),
                "difficulty", skill.getDifficultyLevel().toString()
            ))
            .collect(Collectors.toList()));

        List<Map<String, Object>> edges = new ArrayList<>();
        for (Skill skill : allSkills) {
            for (SkillDependency dep : skill.getDependencies()) {
                edges.add(Map.of(
                    "from", dep.getPrerequisiteSkill().getSkillId(),
                    "to", skill.getSkillId(),
                    "type", dep.getDependencyType().toString()
                ));
            }
        }
        graph.put("edges", edges);

        return graph;
    }

    /**
     * Get user's personalized skill map with proficiency overlay
     */
    public Map<String, Object> getUserSkillMap(Integer userId) {
        List<UserSkill> userSkills = userSkillRepo.findByUserId(userId);
        List<Skill> allSkills = skillRepo.findAll();

        // Overlay user proficiency on nodes
        Map<Integer, Integer> proficiencyMap = userSkills.stream()
            .collect(Collectors.toMap(
                us -> us.getSkill().getSkillId(),
                UserSkill::getProficiencyLevel
            ));

        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", allSkills.stream()
            .map(skill -> {
                Map<String, Object> node = new HashMap<>();
                node.put("id", skill.getSkillId());
                node.put("name", skill.getName());
                node.put("category", skill.getCategory());
                node.put("difficulty", skill.getDifficultyLevel().toString());
                node.put("proficiency", proficiencyMap.getOrDefault(skill.getSkillId(), 0));
                node.put("acquired", proficiencyMap.containsKey(skill.getSkillId()));
                return node;
            })
            .collect(Collectors.toList()));

        List<Map<String, Object>> edges = new ArrayList<>();
        for (Skill skill : allSkills) {
            for (SkillDependency dep : skill.getDependencies()) {
                edges.add(Map.of(
                    "from", dep.getPrerequisiteSkill().getSkillId(),
                    "to", skill.getSkillId(),
                    "type", dep.getDependencyType().toString()
                ));
            }
        }
        graph.put("edges", edges);

        return graph;
    }

    /**
     * Get skill learning path (prerequisites in order)
     */
    public List<String> getSkillLearningPath(String targetSkillName) {
        Optional<Skill> target = skillRepo.findByName(targetSkillName);
        if (target.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> path = new ArrayList<>();
        buildDependencyPath(target.get(), path, new HashSet<>());
        Collections.reverse(path);
        return path;
    }

    private void buildDependencyPath(Skill skill, List<String> path, Set<Integer> visited) {
        if (visited.contains(skill.getSkillId())) return;
        visited.add(skill.getSkillId());

        for (SkillDependency dep : skill.getDependencies()) {
            if (dep.getDependencyType() == SkillDependency.DependencyType.required) {
                buildDependencyPath(dep.getPrerequisiteSkill(), path, visited);
            }
        }

        path.add(skill.getName());
    }

    /**
     * Update user skill proficiency with evidence
     */
    @Transactional
    public UserSkill updateUserSkill(Integer userId, String skillName, Integer proficiency,
                                      String evidence, String evidenceType) {
        Skill skill = skillRepo.findByName(skillName)
            .orElseThrow(() -> new RuntimeException("Skill not found: " + skillName));

        Optional<UserSkill> existing = userSkillRepo.findByUserIdAndSkillId(userId, skill.getSkillId());

        UserSkill userSkill;
        if (existing.isPresent()) {
            userSkill = existing.get();
            userSkill.setProficiencyLevel(proficiency);
        } else {
            userSkill = new UserSkill(userId, skill, proficiency);
        }

        if (evidence != null) {
            userSkill.setEvidenceText(evidence);
        }
        if (evidenceType != null) {
            userSkill.setEvidenceType(UserSkill.EvidenceType.valueOf(evidenceType));
        }

        return userSkillRepo.save(userSkill);
    }

    /**
     * Get skills by category
     */
    public Map<String, List<String>> getSkillsByCategory() {
        List<Skill> all = skillRepo.findAll();
        return all.stream()
            .collect(Collectors.groupingBy(
                Skill::getCategory,
                Collectors.mapping(Skill::getName, Collectors.toList())
            ));
    }

    /**
     * Search skills by keyword
     */
    public List<Skill> searchSkills(String keyword) {
        return skillRepo.searchByKeyword(keyword);
    }

    /**
     * Get skill path for a specific goal
     * Extracts relevant skills from goal description and returns their learning paths
     */
    public Map<String, Object> getSkillPathForGoal(String goalDescription) {
        List<Skill> allSkills = skillRepo.findAll();

        // Find skills mentioned in the goal description
        List<Skill> relevantSkills = allSkills.stream()
            .filter(skill -> goalDescription.toLowerCase().contains(skill.getName().toLowerCase()))
            .toList();

        // Build paths for each relevant skill
        Map<String, List<String>> paths = new HashMap<>();
        for (Skill skill : relevantSkills) {
            List<String> path = getSkillLearningPath(skill.getName());
            paths.put(skill.getName(), path);
        }

        return Map.of(
            "goal", goalDescription,
            "relevantSkills", relevantSkills.stream().map(Skill::getName).toList(),
            "paths", paths
        );
    }
}
