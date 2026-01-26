package com.careermappro.services;

import com.careermappro.entities.Skill;
import com.careermappro.entities.SkillDependency;
import com.careermappro.entities.UserSkill;
import com.careermappro.repositories.SkillRepository;
import com.careermappro.repositories.SkillDependencyRepository;
import com.careermappro.repositories.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningPathService {

    private final SkillRepository skillRepo;
    private final SkillDependencyRepository dependencyRepo;
    private final UserSkillRepository userSkillRepo;

    public LearningPathService(SkillRepository skillRepo,
                              SkillDependencyRepository dependencyRepo,
                              UserSkillRepository userSkillRepo) {
        this.skillRepo = skillRepo;
        this.dependencyRepo = dependencyRepo;
        this.userSkillRepo = userSkillRepo;
    }

    /**
     * Generate a complete learning path for a specific goal
     * Returns: completed skills, current focus, next steps, missing prerequisites
     */
    public Map<String, Object> generateGoalLearningPath(String goalDescription, Integer userId) {
        // Find all skills mentioned in the goal
        List<Skill> allSkills = skillRepo.findAll();
        List<Skill> goalSkills = findGoalRelevantSkills(goalDescription, allSkills);

        // Get user's current skill proficiencies
        Map<Integer, Integer> userProficiencies = getUserProficiencyMap(userId);

        // Build complete dependency graph
        Map<Integer, List<Integer>> dependencyGraph = buildDependencyGraph();

        // Calculate learning path
        Map<String, List<Map<String, Object>>> pathSegments = new HashMap<>();

        // 1. COMPLETED: Skills user has mastered (proficiency >= 70)
        List<Map<String, Object>> completed = new ArrayList<>();

        // 2. IN_PROGRESS: Skills user is currently learning (proficiency 30-69)
        List<Map<String, Object>> inProgress = new ArrayList<>();

        // 3. NEXT_UP: Skills ready to learn (prerequisites met, not started)
        List<Map<String, Object>> nextUp = new ArrayList<>();

        // 4. BLOCKED: Skills needed but prerequisites not met
        List<Map<String, Object>> blocked = new ArrayList<>();

        // 5. MISSING_PREREQUISITES: Prerequisites user skipped
        List<Map<String, Object>> missingPrereqs = new ArrayList<>();

        for (Skill skill : goalSkills) {
            int proficiency = userProficiencies.getOrDefault(skill.getSkillId(), 0);
            List<Integer> prerequisites = dependencyGraph.getOrDefault(skill.getSkillId(), new ArrayList<>());

            // Check if prerequisites are met
            boolean prereqsMet = checkPrerequisitesMet(prerequisites, userProficiencies);
            int unmetPrereqCount = countUnmetPrerequisites(prerequisites, userProficiencies);

            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("skillId", skill.getSkillId());
            skillInfo.put("name", skill.getName());
            skillInfo.put("category", skill.getCategory());
            skillInfo.put("difficulty", skill.getDifficultyLevel().toString());
            skillInfo.put("proficiency", proficiency);
            skillInfo.put("targetProficiency", getTargetProficiency(skill.getDifficultyLevel()));
            skillInfo.put("prerequisites", getPrerequisiteNames(prerequisites));
            skillInfo.put("unmetPrerequisites", unmetPrereqCount);

            if (proficiency >= 70) {
                skillInfo.put("status", "COMPLETED");
                skillInfo.put("message", "✅ Mastered!");
                completed.add(skillInfo);
            } else if (proficiency >= 30) {
                skillInfo.put("status", "IN_PROGRESS");
                skillInfo.put("progress", proficiency);
                skillInfo.put("message", "🔥 Keep going! " + (70 - proficiency) + "% to mastery");
                inProgress.add(skillInfo);
            } else if (proficiency > 0 && !prereqsMet) {
                // Started but missing prerequisites
                skillInfo.put("status", "MISSING_PREREQS");
                skillInfo.put("message", "⚠️ Review prerequisites first!");
                missingPrereqs.add(skillInfo);
            } else if (prereqsMet) {
                skillInfo.put("status", "NEXT_UP");
                skillInfo.put("message", "🎯 Ready to start!");
                skillInfo.put("priority", calculatePriority(skill, prerequisites, userProficiencies));
                nextUp.add(skillInfo);
            } else {
                skillInfo.put("status", "BLOCKED");
                skillInfo.put("message", "🔒 Complete " + unmetPrereqCount + " prerequisite(s) first");
                blocked.add(skillInfo);
            }
        }

        // Sort by priority
        nextUp.sort((a, b) -> Integer.compare(
            (Integer) b.getOrDefault("priority", 0),
            (Integer) a.getOrDefault("priority", 0)
        ));

        pathSegments.put("completed", completed);
        pathSegments.put("inProgress", inProgress);
        pathSegments.put("nextUp", nextUp);
        pathSegments.put("blocked", blocked);
        pathSegments.put("missingPrerequisites", missingPrereqs);

        // Calculate overall stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSkills", goalSkills.size());
        stats.put("completedCount", completed.size());
        stats.put("inProgressCount", inProgress.size());
        stats.put("nextUpCount", nextUp.size());
        stats.put("blockedCount", blocked.size());
        stats.put("completionPercentage", goalSkills.isEmpty() ? 0 : (completed.size() * 100.0 / goalSkills.size()));

        Map<String, Object> result = new HashMap<>();
        result.put("goal", goalDescription);
        result.put("stats", stats);
        result.put("path", pathSegments);
        result.put("recommendedFocus", getRecommendedFocus(inProgress, nextUp));

        return result;
    }

    private List<Skill> findGoalRelevantSkills(String goalDescription, List<Skill> allSkills) {
        String lowerGoal = goalDescription.toLowerCase();
        List<Skill> relevant = new ArrayList<>();

        for (Skill skill : allSkills) {
            if (lowerGoal.contains(skill.getName().toLowerCase())) {
                relevant.add(skill);
                // Also add all prerequisites
                addPrerequisitesRecursively(skill, relevant, allSkills);
            }
        }

        // If no specific skills found, use category matching
        if (relevant.isEmpty()) {
            if (lowerGoal.contains("frontend") || lowerGoal.contains("react") || lowerGoal.contains("ui")) {
                relevant.addAll(allSkills.stream()
                    .filter(s -> s.getCategory().equals("Frontend"))
                    .collect(Collectors.toList()));
            } else if (lowerGoal.contains("backend") || lowerGoal.contains("api") || lowerGoal.contains("server")) {
                relevant.addAll(allSkills.stream()
                    .filter(s -> s.getCategory().equals("Backend"))
                    .collect(Collectors.toList()));
            } else if (lowerGoal.contains("devops") || lowerGoal.contains("deploy") || lowerGoal.contains("cloud")) {
                relevant.addAll(allSkills.stream()
                    .filter(s -> s.getCategory().equals("DevOps"))
                    .collect(Collectors.toList()));
            } else {
                // Default: return all skills
                relevant.addAll(allSkills);
            }
        }

        return relevant.stream().distinct().collect(Collectors.toList());
    }

    private void addPrerequisitesRecursively(Skill skill, List<Skill> collected, List<Skill> allSkills) {
        List<SkillDependency> deps = dependencyRepo.findBySkill(skill);
        for (SkillDependency dep : deps) {
            if (dep.getDependencyType() == SkillDependency.DependencyType.required) {
                Skill prereq = dep.getPrerequisiteSkill();
                if (!collected.contains(prereq)) {
                    collected.add(prereq);
                    addPrerequisitesRecursively(prereq, collected, allSkills);
                }
            }
        }
    }

    private Map<Integer, Integer> getUserProficiencyMap(Integer userId) {
        List<UserSkill> userSkills = userSkillRepo.findByUserId(userId);
        Map<Integer, Integer> map = new HashMap<>();
        for (UserSkill us : userSkills) {
            map.put(us.getSkill().getSkillId(), us.getProficiencyLevel());
        }
        return map;
    }

    private Map<Integer, List<Integer>> buildDependencyGraph() {
        List<SkillDependency> allDeps = dependencyRepo.findAll();
        Map<Integer, List<Integer>> graph = new HashMap<>();

        for (SkillDependency dep : allDeps) {
            if (dep.getDependencyType() == SkillDependency.DependencyType.required) {
                int skillId = dep.getSkill().getSkillId();
                int prereqId = dep.getPrerequisiteSkill().getSkillId();
                graph.computeIfAbsent(skillId, k -> new ArrayList<>()).add(prereqId);
            }
        }

        return graph;
    }

    private boolean checkPrerequisitesMet(List<Integer> prerequisites, Map<Integer, Integer> userProf) {
        for (Integer prereqId : prerequisites) {
            int proficiency = userProf.getOrDefault(prereqId, 0);
            if (proficiency < 50) { // Need at least 50% proficiency in prerequisites
                return false;
            }
        }
        return true;
    }

    private int countUnmetPrerequisites(List<Integer> prerequisites, Map<Integer, Integer> userProf) {
        int count = 0;
        for (Integer prereqId : prerequisites) {
            int proficiency = userProf.getOrDefault(prereqId, 0);
            if (proficiency < 50) {
                count++;
            }
        }
        return count;
    }

    private List<String> getPrerequisiteNames(List<Integer> prerequisiteIds) {
        return prerequisiteIds.stream()
            .map(id -> skillRepo.findById(id).map(Skill::getName).orElse("Unknown"))
            .collect(Collectors.toList());
    }

    private int getTargetProficiency(Skill.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case Beginner -> 70;
            case Intermediate -> 75;
            case Advanced -> 85;
            case Expert -> 95;
        };
    }

    private int calculatePriority(Skill skill, List<Integer> prerequisites, Map<Integer, Integer> userProf) {
        int priority = 50; // Base priority

        // Higher priority for beginner skills
        if (skill.getDifficultyLevel() == Skill.DifficultyLevel.Beginner) {
            priority += 30;
        }

        // Higher priority if all prerequisites are well-mastered
        int avgPrereqProf = 0;
        if (!prerequisites.isEmpty()) {
            for (Integer prereqId : prerequisites) {
                avgPrereqProf += userProf.getOrDefault(prereqId, 0);
            }
            avgPrereqProf /= prerequisites.size();
            priority += (avgPrereqProf / 10); // Up to +10 points
        }

        return priority;
    }

    private List<Map<String, Object>> getRecommendedFocus(
            List<Map<String, Object>> inProgress,
            List<Map<String, Object>> nextUp) {

        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Recommend finishing in-progress skills first
        if (!inProgress.isEmpty()) {
            Map<String, Object> rec1 = new HashMap<>();
            rec1.put("action", "FINISH_IN_PROGRESS");
            rec1.put("message", "Complete " + inProgress.size() + " skill(s) you've already started");
            rec1.put("skills", inProgress.stream()
                .map(s -> s.get("name"))
                .collect(Collectors.toList()));
            recommendations.add(rec1);
        }

        // Recommend top 3 next skills
        if (!nextUp.isEmpty()) {
            Map<String, Object> rec2 = new HashMap<>();
            rec2.put("action", "START_NEW");
            rec2.put("message", "Start learning these ready skills");
            rec2.put("skills", nextUp.stream()
                .limit(3)
                .map(s -> s.get("name"))
                .collect(Collectors.toList()));
            recommendations.add(rec2);
        }

        return recommendations;
    }
}
