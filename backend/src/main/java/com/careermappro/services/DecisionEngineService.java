package com.careermappro.services;

import com.careermappro.dto.FrontierNode;
import com.careermappro.dto.RecommendedAction;
import com.careermappro.entities.*;
import com.careermappro.entities.UserSkillState.SkillStatus;
import com.careermappro.repositories.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DecisionEngineService {

    private final UserSkillStateRepository userSkillStateRepo;
    private final SkillNodeRepository skillNodeRepo;
    private final PrereqEdgeRepository prereqEdgeRepo;
    private final RoleSkillRepository roleSkillRepo;

    public DecisionEngineService(
            UserSkillStateRepository userSkillStateRepo,
            SkillNodeRepository skillNodeRepo,
            PrereqEdgeRepository prereqEdgeRepo,
            RoleSkillRepository roleSkillRepo) {
        this.userSkillStateRepo = userSkillStateRepo;
        this.skillNodeRepo = skillNodeRepo;
        this.prereqEdgeRepo = prereqEdgeRepo;
        this.roleSkillRepo = roleSkillRepo;
    }

    /**
     * Select ONE next action for the user based on frontier scoring
     */
    public RecommendedAction selectNextAction(Integer userId, Integer roleId) {
        // Get frontier nodes (ACTIVE + high-value INFERRED)
        List<FrontierNode> frontier = computeFrontier(userId, roleId);

        if (frontier.isEmpty()) {
            return createDefaultAction();
        }

        // Score each node
        for (FrontierNode node : frontier) {
            double score = computeScore(node, userId, roleId);
            node.setScore(score);
        }

        // Sort by score descending
        frontier.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // Take top node
        FrontierNode topNode = frontier.get(0);

        // Determine action type
        String actionType = selectActionType(topNode);

        return buildAction(actionType, topNode);
    }

    /**
     * Compute frontier: ACTIVE nodes + INFERRED nodes that are close to becoming ACTIVE
     */
    public List<FrontierNode> computeFrontier(Integer userId, Integer roleId) {
        List<FrontierNode> frontier = new ArrayList<>();

        // Get all user skill states
        List<UserSkillState> allStates = userSkillStateRepo.findByUserId(userId);
        Map<Integer, UserSkillState> stateMap = allStates.stream()
                .collect(Collectors.toMap(UserSkillState::getSkillId, s -> s));

        // Get required skills for role
        List<RoleSkill> roleSkills = roleSkillRepo.findByRoleId(roleId);

        for (RoleSkill rs : roleSkills) {
            Integer skillId = rs.getSkill().getSkillId();
            UserSkillState state = stateMap.get(skillId);

            // Include ACTIVE nodes
            if (state != null && state.getStatus() == SkillStatus.ACTIVE) {
                FrontierNode node = new FrontierNode(skillId, rs.getSkill().getName(), state.getStatus(), state.getConfidence());
                node.setDemandWeight(rs.getWeight() != null ? rs.getWeight() : 0.1);
                frontier.add(node);
            }
            // Include INFERRED nodes with partial prereq satisfaction
            else if (state != null && state.getStatus() == SkillStatus.INFERRED) {
                FrontierNode node = new FrontierNode(skillId, rs.getSkill().getName(), state.getStatus(), state.getConfidence());
                node.setDemandWeight(rs.getWeight() != null ? rs.getWeight() : 0.1);
                frontier.add(node);
            }
            // Include UNSEEN high-demand skills
            else if (state == null || state.getStatus() == SkillStatus.UNSEEN) {
                if (rs.getWeight() != null && rs.getWeight() > 0.15) { // High demand threshold
                    FrontierNode node = new FrontierNode(skillId, rs.getSkill().getName(), SkillStatus.UNSEEN, 0.0);
                    node.setDemandWeight(rs.getWeight());
                    frontier.add(node);
                }
            }
        }

        return frontier;
    }

    /**
     * Score = DemandWeight * UnlockPotential * (1 - Confidence) * Feasibility
     */
    private double computeScore(FrontierNode node, Integer userId, Integer roleId) {
        double demandWeight = node.getDemandWeight() != null ? node.getDemandWeight() : 0.1;
        double unlockPotential = computeUnlockPotential(node.getSkillId(), roleId);
        double uncertainty = 1.0 - (node.getConfidence() != null ? node.getConfidence() : 0.0);
        double feasibility = computeFeasibility(node, userId);

        node.setUnlockPotential(unlockPotential);

        return demandWeight * unlockPotential * uncertainty * feasibility;
    }

    /**
     * UnlockPotential: Weighted sum of downstream skills this skill unlocks
     */
    private double computeUnlockPotential(Integer skillId, Integer roleId) {
        // Find all skills that have this skill as a prereq
        List<PrereqEdge> downstreamEdges = prereqEdgeRepo.findByFromSkillId(skillId);

        if (downstreamEdges.isEmpty()) {
            return 1.0; // Leaf skill
        }

        // Get role skills to check if downstream skills are in demand
        List<RoleSkill> roleSkills = roleSkillRepo.findByRoleId(roleId);
        Map<Integer, Double> roleSkillWeights = roleSkills.stream()
                .collect(Collectors.toMap(rs -> rs.getSkill().getSkillId(), rs -> rs.getWeight() != null ? rs.getWeight() : 0.1));

        double totalPotential = 0.0;
        for (PrereqEdge edge : downstreamEdges) {
            double downstreamWeight = roleSkillWeights.getOrDefault(edge.getToSkillId(), 0.0);
            totalPotential += edge.getStrength() * downstreamWeight;
        }

        return Math.min(totalPotential * 5.0, 10.0); // Scale and cap at 10
    }

    /**
     * Feasibility: Check if prereqs are mostly satisfied
     */
    private double computeFeasibility(FrontierNode node, Integer userId) {
        List<PrereqEdge> prereqs = prereqEdgeRepo.findByToSkillId(node.getSkillId());

        if (prereqs.isEmpty()) {
            return 1.0; // No prereqs = fully feasible
        }

        List<Integer> prereqSkillIds = prereqs.stream()
                .map(PrereqEdge::getFromSkillId)
                .collect(Collectors.toList());

        List<UserSkillState> prereqStates = userSkillStateRepo.findByUserIdAndSkillIdIn(userId, prereqSkillIds);
        Map<Integer, Double> prereqConfidence = prereqStates.stream()
                .collect(Collectors.toMap(UserSkillState::getSkillId, UserSkillState::getConfidence));

        double totalStrength = 0.0;
        double satisfiedStrength = 0.0;

        for (PrereqEdge edge : prereqs) {
            totalStrength += edge.getStrength();
            double conf = prereqConfidence.getOrDefault(edge.getFromSkillId(), 0.0);
            satisfiedStrength += edge.getStrength() * conf;
        }

        return totalStrength > 0 ? satisfiedStrength / totalStrength : 0.5;
    }

    /**
     * Determine action type based on node characteristics
     */
    private String selectActionType(FrontierNode node) {
        double confidence = node.getConfidence() != null ? node.getConfidence() : 0.0;

        // Low confidence on gating node → PROBE (quiz)
        if (confidence < 0.5) {
            return "PROBE";
        }
        // Medium confidence → BUILD (mini-task)
        else if (confidence < 0.85) {
            return "BUILD";
        }
        // High confidence but not proved → could APPLY or BUILD
        else {
            // For now, default to BUILD to get to PROVED status
            return "BUILD";
        }
    }

    /**
     * Build RecommendedAction from type and node
     */
    private RecommendedAction buildAction(String type, FrontierNode node) {
        RecommendedAction action = new RecommendedAction();
        action.setType(type);
        action.setSkillId(node.getSkillId());
        action.setSkillName(node.getSkillName());

        switch (type) {
            case "PROBE":
                action.setLabel("Assess " + node.getSkillName());
                action.setEstimatedMinutes(10);
                action.setPayload(Map.of("quizDifficulty", "Intermediate", "numQuestions", 10));
                node.setWhy("Proving this skill unlocks " + String.format("%.0f", node.getUnlockPotential()) + " downstream opportunities");
                break;
            case "BUILD":
                action.setLabel("Build with " + node.getSkillName());
                action.setEstimatedMinutes(30);
                action.setPayload(Map.of("taskType", "mini-project"));
                node.setWhy("Close to proving mastery - build a small project to solidify knowledge");
                break;
            case "APPLY":
                action.setLabel("Apply for roles using " + node.getSkillName());
                action.setEstimatedMinutes(15);
                node.setWhy("Skill proven - ready to apply to opportunities");
                break;
            default:
                action.setLabel("Continue learning " + node.getSkillName());
                action.setEstimatedMinutes(20);
        }

        return action;
    }

    private RecommendedAction createDefaultAction() {
        RecommendedAction action = new RecommendedAction();
        action.setType("EXPLORE");
        action.setLabel("Add evidence to prove your skills");
        action.setEstimatedMinutes(5);
        return action;
    }
}
