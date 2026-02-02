package com.careermappro.controllers;

import com.careermappro.services.PathService;
import com.careermappro.repositories.LearningPathRepository;
import com.careermappro.repositories.PathUnitRepository;
import com.careermappro.repositories.PathStepRepository;
import com.careermappro.repositories.StudyResourceRepository;
import com.careermappro.repositories.PathProgressRepository;
import com.careermappro.entities.LearningPath;
import com.careermappro.entities.PathUnit;
import com.careermappro.entities.PathStep;
import com.careermappro.entities.StudyResource;
import com.careermappro.entities.PathProgress;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
public class PathController {

    private final PathService pathService;
    private final LearningPathRepository learningPathRepository;
    private final PathUnitRepository pathUnitRepository;
    private final PathStepRepository pathStepRepository;
    private final StudyResourceRepository studyResourceRepository;
    private final PathProgressRepository pathProgressRepository;

    public PathController(
            PathService pathService,
            LearningPathRepository learningPathRepository,
            PathUnitRepository pathUnitRepository,
            PathStepRepository pathStepRepository,
            StudyResourceRepository studyResourceRepository,
            PathProgressRepository pathProgressRepository) {
        this.pathService = pathService;
        this.learningPathRepository = learningPathRepository;
        this.pathUnitRepository = pathUnitRepository;
        this.pathStepRepository = pathStepRepository;
        this.studyResourceRepository = studyResourceRepository;
        this.pathProgressRepository = pathProgressRepository;
    }

    /**
     * GET /api/v1/path?userId=X
     * Returns user's path state: primary role, readiness, skill breakdown, next actions
     */
    @GetMapping("/path")
    public Map<String, Object> getPath(@RequestParam Integer userId) {
        return pathService.getPathState(userId);
    }

    /**
     * PUT /api/v1/path/primary-role?userId=X&roleId=Y
     * Sets user's primary role
     */
    @PutMapping("/path/primary-role")
    public Map<String, Object> setPrimaryRole(
        @RequestParam Integer userId,
        @RequestParam Integer roleId
    ) {
        return pathService.setPrimaryRole(userId, roleId);
    }

    /**
     * GET /api/v1/roles
     * Returns all available career roles
     */
    @GetMapping("/roles")
    public List<Map<String, Object>> getRoles() {
        return pathService.getAllRoles();
    }

    /**
     * POST /api/v1/learning-path/generate
     * Generate a new learning path for a user
     */
    @PostMapping("/learning-path/generate")
    public Map<String, Object> generateLearningPath(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("pathId", 1001);
        response.put("userId", request.get("userId"));
        response.put("targetRole", request.get("targetRole"));
        response.put("estimatedDurationWeeks", 12);
        response.put("status", "ACTIVE");
        response.put("createdAt", System.currentTimeMillis());
        response.put("message", "Learning path generated successfully");
        return response;
    }

    /**
     * GET /api/v1/learning-path/{userId}/active
     * Get user's active learning path
     */
    @GetMapping("/learning-path/{userId}/active")
    public Map<String, Object> getActivePath(@PathVariable Integer userId) {
        // Query for active learning paths
        List<LearningPath> activePaths = learningPathRepository.findByUserIdAndStatus(
            userId,
            LearningPath.PathStatus.ACTIVE
        );

        if (activePaths.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "No active learning path found");
            return response;
        }

        // Get the most recent active path
        LearningPath path = activePaths.get(0);

        // Get progress information
        PathProgress progress = pathProgressRepository.findByPathIdAndUserId(path.getPathId(), userId)
            .orElse(new PathProgress(path.getPathId(), userId));

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("pathId", path.getPathId());
        response.put("userId", userId);
        response.put("targetRole", path.getTitle());
        response.put("status", path.getStatus().toString());
        response.put("progressPercentage", progress.getOverallProgress().intValue());
        response.put("unitsCompleted", progress.getCompletedUnits());
        response.put("totalUnits", progress.getTotalUnits());
        response.put("estimatedDurationWeeks", path.getDurationWeeks());
        response.put("startedAt", path.getStartedAt() != null ?
            path.getStartedAt().toString() : null);
        return response;
    }

    /**
     * GET /api/v1/learning-path/{pathId}/current-step?userId=X
     * Get current step in the learning path
     */
    @GetMapping("/learning-path/{pathId}/current-step")
    public Map<String, Object> getCurrentStep(
            @PathVariable Integer pathId,
            @RequestParam Integer userId) {
        // Query path progress to get current step
        PathProgress progress = pathProgressRepository.findByPathIdAndUserId(pathId, userId)
            .orElse(null);

        if (progress == null || progress.getCurrentStepId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "No current step found");
            return response;
        }

        // Get the current step details
        PathStep step = pathStepRepository.findById(progress.getCurrentStepId())
            .orElse(null);

        if (step == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Step not found");
            return response;
        }

        // Get the unit to find unit number
        PathUnit unit = pathUnitRepository.findById(step.getUnitId()).orElse(null);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("stepId", step.getStepId());
        response.put("pathId", pathId);
        response.put("userId", userId);
        response.put("title", step.getTitle());
        response.put("description", step.getDescription());
        response.put("unitNumber", unit != null ? unit.getUnitNumber() : null);
        response.put("stepNumber", step.getStepNumber());
        response.put("status", step.getStatus().toString());
        response.put("progressPercentage", progress.getOverallProgress().intValue());
        response.put("estimatedHours", unit != null ? unit.getEstimatedHours() : null);
        response.put("type", "LEARNING");
        return response;
    }

    /**
     * GET /api/v1/learning-path/{pathId}/units
     * Get all units in a learning path
     */
    @GetMapping("/learning-path/{pathId}/units")
    public Map<String, Object> getPathUnits(@PathVariable Integer pathId) {
        // Query all units for the path ordered by unit number
        List<PathUnit> pathUnits = pathUnitRepository.findByPathIdOrderByUnitNumber(pathId);

        // Convert entities to Map responses
        List<Map<String, Object>> units = pathUnits.stream().map(unit -> {
            Map<String, Object> unitMap = new HashMap<>();
            unitMap.put("unitId", unit.getUnitId());
            unitMap.put("unitNumber", unit.getUnitNumber());
            unitMap.put("title", unit.getTitle());
            unitMap.put("description", unit.getDescription());
            unitMap.put("status", unit.getStatus().toString());

            // Calculate progress percentage for this unit
            long totalSteps = pathStepRepository.countByUnitId(unit.getUnitId());
            long completedSteps = pathStepRepository.countByUnitIdAndStatus(
                unit.getUnitId(),
                PathStep.StepStatus.COMPLETED
            );
            int progressPercentage = totalSteps > 0 ?
                (int) ((completedSteps * 100) / totalSteps) : 0;

            unitMap.put("progressPercentage", progressPercentage);
            unitMap.put("stepsCount", (int) totalSteps);

            return unitMap;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pathId", pathId);
        response.put("totalUnits", units.size());
        response.put("units", units);
        return response;
    }

    /**
     * GET /api/v1/learning-path/steps/{stepId}/resources
     * Get study resources for a specific step
     */
    @GetMapping("/learning-path/steps/{stepId}/resources")
    public Map<String, Object> getStepResources(@PathVariable Integer stepId) {
        // Query study resources ordered by relevance score
        List<StudyResource> studyResources = studyResourceRepository
            .findByStepIdOrderByRelevanceScoreDesc(stepId);

        // Convert entities to Map responses
        List<Map<String, Object>> resources = studyResources.stream().map(resource -> {
            Map<String, Object> resourceMap = new HashMap<>();
            resourceMap.put("resourceId", resource.getResourceId());
            resourceMap.put("title", resource.getTitle());
            resourceMap.put("type", resource.getType().toString());
            resourceMap.put("url", resource.getUrl());
            resourceMap.put("description", resource.getDescription());
            resourceMap.put("estimatedTime", resource.getEstimatedTimeMinutes());
            resourceMap.put("difficulty", resource.getDifficulty().toString());
            resourceMap.put("source", resource.getSource());
            resourceMap.put("author", resource.getAuthor());
            resourceMap.put("relevanceScore", resource.getRelevanceScore());
            resourceMap.put("isHelpful", resource.getIsHelpful());
            resourceMap.put("userRating", resource.getUserRating());
            resourceMap.put("completed", false); // TODO: Track individual resource completion

            return resourceMap;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("stepId", stepId);
        response.put("totalResources", resources.size());
        response.put("resources", resources);
        return response;
    }
}
