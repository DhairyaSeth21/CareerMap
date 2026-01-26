package com.careermappro.services;

import com.careermappro.entities.*;
import com.careermappro.repositories.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final UserRoleRepository userRoleRepository;
    private final RoleSkillRepository roleSkillRepository;
    private final UserSkillStateRepository userSkillStateRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public PathService(
        UserRoleRepository userRoleRepository,
        RoleSkillRepository roleSkillRepository,
        UserSkillStateRepository userSkillStateRepository,
        RoleRepository roleRepository,
        UserRepository userRepository
    ) {
        this.userRoleRepository = userRoleRepository;
        this.roleSkillRepository = roleSkillRepository;
        this.userSkillStateRepository = userSkillStateRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/v1/path
     * Returns user's path state: primary role, readiness, breakdown, next actions
     */
    public Map<String, Object> getPathState(Integer userId) {
        Map<String, Object> response = new HashMap<>();

        // Get user's primary role
        Optional<UserRole> primaryRoleOpt = userRoleRepository.findByUserIdAndIsPrimary(userId, true);

        if (primaryRoleOpt.isEmpty()) {
            // No role selected - return empty state
            response.put("hasRole", false);
            response.put("message", "No primary role selected. Choose a role to get started.");
            response.put("availableRoles", getAllRoles());
            return response;
        }

        UserRole userRole = primaryRoleOpt.get();
        Role role = userRole.getRole();

        response.put("hasRole", true);
        response.put("roleId", role.getId());
        response.put("roleName", role.getName());
        response.put("roleCategory", role.getCategory());

        // Get required skills for this role
        List<RoleSkill> requiredSkills = roleSkillRepository.findByRoleId(role.getId());

        // Get user's current skill levels (EDLSG: using evidenceScore as level proxy)
        List<UserSkillState> userSkills = userSkillStateRepository.findByUserId(userId);
        Map<Integer, Double> userSkillLevels = userSkills.stream()
            .collect(Collectors.toMap(
                UserSkillState::getSkillId,
                uss -> uss.getEvidenceScore() != null ? uss.getEvidenceScore() : 0.0
            ));

        // Calculate readiness (0-10 scale)
        double readiness = calculateReadiness(requiredSkills, userSkillLevels);
        response.put("readiness", Math.round(readiness * 10.0) / 10.0);

        // Breakdown by skill
        List<Map<String, Object>> skillBreakdown = requiredSkills.stream()
            .map(rs -> {
                Map<String, Object> skill = new HashMap<>();
                Skill s = rs.getSkill();
                skill.put("skillId", s.getSkillId());
                skill.put("skillName", s.getName());
                skill.put("requiredLevel", rs.getRequiredLevel());
                skill.put("currentLevel", userSkillLevels.getOrDefault(s.getSkillId(), 0.0));
                skill.put("weight", rs.getWeight());
                double gap = rs.getRequiredLevel() - userSkillLevels.getOrDefault(s.getSkillId(), 0.0);
                skill.put("gap", Math.max(0, gap));
                return skill;
            })
            .collect(Collectors.toList());

        response.put("skillBreakdown", skillBreakdown);

        // Determine next 1-3 actions
        List<Map<String, Object>> nextActions = determineNextActions(skillBreakdown);
        response.put("nextActions", nextActions);

        return response;
    }

    /**
     * PUT /api/v1/path/primary-role
     * Sets user's primary role
     */
    public Map<String, Object> setPrimaryRole(Integer userId, Integer roleId) {
        // Check if role exists
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return Map.of("success", false, "message", "Role not found");
        }

        // Clear existing primary role
        List<UserRole> existingRoles = userRoleRepository.findByUserId(userId);
        for (UserRole ur : existingRoles) {
            ur.setIsPrimary(false);
            userRoleRepository.save(ur);
        }

        // Set or create new primary role
        UserRole userRole = existingRoles.stream()
            .filter(ur -> ur.getRole().getId().equals(roleId))
            .findFirst()
            .orElse(null);

        if (userRole == null) {
            // Create new user role
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(roleOpt.get());
        }

        userRole.setIsPrimary(true);
        userRoleRepository.save(userRole);

        return Map.of(
            "success", true,
            "message", "Primary role set successfully",
            "roleId", roleId,
            "roleName", roleOpt.get().getName()
        );
    }

    /**
     * GET all available roles
     */
    public List<Map<String, Object>> getAllRoles() {
        return roleRepository.findAll().stream()
            .map(role -> {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", role.getId());
                roleMap.put("name", role.getName());
                roleMap.put("category", role.getCategory());
                roleMap.put("description", role.getDescription());
                roleMap.put("icon", role.getIcon());
                return roleMap;
            })
            .collect(Collectors.toList());
    }

    // ===== PRIVATE HELPERS =====

    private double calculateReadiness(List<RoleSkill> requiredSkills, Map<Integer, Double> userSkillLevels) {
        if (requiredSkills.isEmpty()) {
            return 0.0;
        }

        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;

        for (RoleSkill rs : requiredSkills) {
            double currentLevel = userSkillLevels.getOrDefault(rs.getSkill().getSkillId(), 0.0);
            double requiredLevel = rs.getRequiredLevel();
            double weight = rs.getWeight();

            // Calculate normalized score (0-10) for this skill
            double skillScore = Math.min(10.0, (currentLevel / requiredLevel) * 10.0);

            totalWeightedScore += skillScore * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
    }

    private List<Map<String, Object>> determineNextActions(List<Map<String, Object>> skillBreakdown) {
        // Sort by gap (largest gaps first) and low confidence
        List<Map<String, Object>> prioritized = skillBreakdown.stream()
            .filter(s -> (double) s.get("gap") > 0)
            .sorted((a, b) -> Double.compare((double) b.get("gap"), (double) a.get("gap")))
            .limit(3)
            .collect(Collectors.toList());

        List<Map<String, Object>> actions = new ArrayList<>();
        int actionNumber = 1;

        for (Map<String, Object> skill : prioritized) {
            Map<String, Object> action = new HashMap<>();
            action.put("actionNumber", actionNumber++);
            action.put("type", "ASSESS_SKILL");
            action.put("skillName", skill.get("skillName"));
            action.put("skillId", skill.get("skillId"));
            action.put("reason", "Large gap: " + String.format("%.1f", skill.get("gap")) + " points");
            action.put("actionText", "Take a quiz to assess your " + skill.get("skillName") + " skills");
            actions.add(action);
        }

        if (actions.isEmpty()) {
            // All skills met - suggest finding opportunities
            Map<String, Object> action = new HashMap<>();
            action.put("actionNumber", 1);
            action.put("type", "FIND_OPPORTUNITY");
            action.put("actionText", "You're ready! Paste a job description to see how you match");
            action.put("reason", "All required skills met");
            actions.add(action);
        }

        return actions;
    }
}
