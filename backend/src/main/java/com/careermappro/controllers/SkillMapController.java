package com.careermappro.controllers;

import com.careermappro.entities.Skill;
import com.careermappro.entities.UserSkill;
import com.careermappro.services.SkillMapService;
import com.careermappro.services.LearningPathService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
@RequestMapping("/api/skillmap")
public class SkillMapController {

    private final SkillMapService skillMapService;
    private final LearningPathService learningPathService;

    public SkillMapController(SkillMapService skillMapService, LearningPathService learningPathService) {
        this.skillMapService = skillMapService;
        this.learningPathService = learningPathService;
    }

    /**
     * GET /api/skillmap/graph
     * Returns full skill graph with all nodes and edges
     */
    @GetMapping("/graph")
    public Map<String, Object> getSkillGraph() {
        return skillMapService.getSkillGraph();
    }

    /**
     * GET /api/skillmap/user/{userId}
     * Returns user's personalized skill map with proficiency overlay
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserSkillMap(@PathVariable Integer userId) {
        return skillMapService.getUserSkillMap(userId);
    }

    /**
     * GET /api/skillmap/path/{skillName}
     * Returns learning path (prerequisites in order) for a skill
     */
    @GetMapping("/path/{skillName}")
    public Map<String, Object> getSkillPath(@PathVariable String skillName) {
        List<String> path = skillMapService.getSkillLearningPath(skillName);
        return Map.of("skill", skillName, "learningPath", path);
    }

    /**
     * POST /api/skillmap/update
     * Update user skill with evidence
     */
    @PostMapping("/update")
    public UserSkill updateUserSkill(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String skillName = (String) request.get("skillName");
        Integer proficiency = (Integer) request.get("proficiency");
        String evidence = (String) request.get("evidence");
        String evidenceType = (String) request.get("evidenceType");

        return skillMapService.updateUserSkill(userId, skillName, proficiency, evidence, evidenceType);
    }

    /**
     * GET /api/skillmap/categories
     * Get all skills organized by category
     */
    @GetMapping("/categories")
    public Map<String, List<String>> getSkillsByCategory() {
        return skillMapService.getSkillsByCategory();
    }

    /**
     * GET /api/skillmap/search
     * Search skills by keyword
     */
    @GetMapping("/search")
    public List<Skill> searchSkills(@RequestParam String q) {
        return skillMapService.searchSkills(q);
    }

    /**
     * GET /api/skillmap/goal-path
     * Get skill path for a specific goal
     */
    @GetMapping("/goal-path")
    public Map<String, Object> getGoalSkillPath(@RequestParam String goal) {
        return skillMapService.getSkillPathForGoal(goal);
    }

    /**
     * GET /api/skillmap/learning-path
     * Get complete learning path with status for a goal
     * Returns: completed, in-progress, next-up, blocked, missing prerequisites
     */
    @GetMapping("/learning-path")
    public Map<String, Object> getLearningPath(@RequestParam String goal, @RequestParam Integer userId) {
        return learningPathService.generateGoalLearningPath(goal, userId);
    }
}
