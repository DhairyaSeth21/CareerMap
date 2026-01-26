package com.careermappro.controllers;

import com.careermappro.dto.FrontierNode;
import com.careermappro.services.DecisionEngineService;
import com.careermappro.repositories.UserRoleRepository;
import com.careermappro.entities.UserRole;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
public class FrontierV2Controller {

    private final DecisionEngineService decisionEngineService;
    private final UserRoleRepository userRoleRepository;

    public FrontierV2Controller(
            DecisionEngineService decisionEngineService,
            UserRoleRepository userRoleRepository) {
        this.decisionEngineService = decisionEngineService;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * GET /api/v2/frontier?userId=X
     * Returns frontier data for a user's current primary role
     */
    @GetMapping("/frontier")
    public Map<String, Object> getFrontier(@RequestParam Integer userId) {
        Map<String, Object> response = new HashMap<>();

        // Get user's primary role
        Optional<UserRole> primaryRoleOpt = userRoleRepository.findByUserIdAndIsPrimary(userId, true);

        if (primaryRoleOpt.isEmpty()) {
            // Return structure that frontend expects even for error case
            response.put("error", "No primary role set for user");
            response.put("userId", userId);
            response.put("frontierPreview", List.of());
            response.put("role", Map.of("id", 0, "name", "Unknown", "category", "Unknown"));
            response.put("recommendedAction", Map.of(
                "type", "SETUP",
                "label", "Set up your profile",
                "skillId", 0,
                "skillName", "",
                "estimatedMinutes", 5,
                "payload", Map.of()
            ));
            return response;
        }

        UserRole userRole = primaryRoleOpt.get();
        Integer roleId = userRole.getRole().getId();

        // Compute frontier
        List<FrontierNode> frontier = decisionEngineService.computeFrontier(userId, roleId);

        // Convert FrontierNode list to format frontend expects
        List<Map<String, Object>> frontierPreview = frontier.stream()
            .map(node -> {
                Map<String, Object> skillData = new HashMap<>();
                skillData.put("skillId", node.getSkillId());
                skillData.put("skillName", node.getSkillName());
                skillData.put("status", node.getStatus().toString());
                skillData.put("confidence", node.getConfidence());
                skillData.put("demandWeight", node.getDemandWeight());
                return skillData;
            })
            .toList();

        // Build role info
        Map<String, Object> roleInfo = new HashMap<>();
        roleInfo.put("id", userRole.getRole().getId());
        roleInfo.put("name", userRole.getRole().getName());
        roleInfo.put("category", userRole.getRole().getCategory());

        // Find recommended action (first ACTIVE or INFERRED node with high demand)
        FrontierNode recommended = frontier.stream()
            .filter(n -> n.getStatus() == com.careermappro.entities.UserSkillState.SkillStatus.ACTIVE ||
                         n.getStatus() == com.careermappro.entities.UserSkillState.SkillStatus.INFERRED)
            .findFirst()
            .orElse(frontier.isEmpty() ? null : frontier.get(0));

        Map<String, Object> recommendedAction = new HashMap<>();
        if (recommended != null) {
            recommendedAction.put("type", "PROBE");
            recommendedAction.put("label", "Take " + recommended.getSkillName() + " Assessment");
            recommendedAction.put("skillId", recommended.getSkillId());
            recommendedAction.put("skillName", recommended.getSkillName());
            recommendedAction.put("estimatedMinutes", 15);
            recommendedAction.put("payload", Map.of());
        } else {
            recommendedAction.put("type", "BUILD");
            recommendedAction.put("label", "No immediate actions");
            recommendedAction.put("skillId", 0);
            recommendedAction.put("skillName", "");
            recommendedAction.put("estimatedMinutes", 0);
            recommendedAction.put("payload", Map.of());
        }

        // Build response
        response.put("userId", userId);
        response.put("roleId", roleId);
        response.put("frontierPreview", frontierPreview);
        response.put("role", roleInfo);
        response.put("recommendedAction", recommendedAction);

        if (recommended != null) {
            Map<String, Object> highlighted = new HashMap<>();
            highlighted.put("id", recommended.getSkillId());
            highlighted.put("name", recommended.getSkillName());
            highlighted.put("status", recommended.getStatus().toString());
            highlighted.put("confidence", recommended.getConfidence());
            highlighted.put("why", "Next step in your skill development path");
            response.put("highlightedSkill", highlighted);
        }

        return response;
    }
}
