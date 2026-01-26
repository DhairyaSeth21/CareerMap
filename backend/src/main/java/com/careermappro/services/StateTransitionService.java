package com.careermappro.services;

import com.careermappro.entities.PrereqEdge;
import com.careermappro.entities.SkillNode;
import com.careermappro.entities.UserSkillState;
import com.careermappro.entities.UserSkillState.SkillStatus;
import com.careermappro.repositories.PrereqEdgeRepository;
import com.careermappro.repositories.SkillNodeRepository;
import com.careermappro.repositories.UserSkillStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StateTransitionService {

    private final UserSkillStateRepository userSkillStateRepo;
    private final PrereqEdgeRepository prereqEdgeRepo;
    private final SkillNodeRepository skillNodeRepo;

    private static final double HARD_PREREQ_THRESHOLD = 0.9;
    private static final double SOFT_PREREQ_THRESHOLD = 0.65;
    private static final double PROVED_SUPPORT_THRESHOLD = 0.7;

    public StateTransitionService(
            UserSkillStateRepository userSkillStateRepo,
            PrereqEdgeRepository prereqEdgeRepo,
            SkillNodeRepository skillNodeRepo) {
        this.userSkillStateRepo = userSkillStateRepo;
        this.prereqEdgeRepo = prereqEdgeRepo;
        this.skillNodeRepo = skillNodeRepo;
    }

    @Transactional
    public void updateStateFromEvidence(Integer userId, Integer skillId, double support, String evidenceType) {
        System.out.println("  --> StateTransitionService.updateStateFromEvidence()");
        System.out.println("      userId=" + userId + ", skillId=" + skillId + ", support=" + String.format("%.2f", support) + ", evidenceType=" + evidenceType);

        UserSkillState state = userSkillStateRepo.findByUserIdAndSkillId(userId, skillId)
                .orElseGet(() -> {
                    System.out.println("      NO EXISTING STATE - Creating new UserSkillState");
                    UserSkillState newState = new UserSkillState();
                    newState.setUserId(userId);
                    newState.setSkillId(skillId);
                    return newState;
                });

        SkillStatus oldStatus = state.getStatus();
        double oldConfidence = state.getConfidence();

        System.out.println("      Current state: " + oldStatus + " (confidence=" + String.format("%.2f", oldConfidence) + ")");

        LocalDateTime now = LocalDateTime.now();
        SkillNode skillNode = skillNodeRepo.findById(skillId).orElse(null);
        int decayDays = (skillNode != null) ? skillNode.getDecayHalfLifeDays() : 180;

        boolean isHighTrust = isHighTrustEvidence(evidenceType);
        System.out.println("      High-trust evidence: " + isHighTrust);
        System.out.println("      Support > PROVED_THRESHOLD (" + PROVED_SUPPORT_THRESHOLD + "): " + (support > PROVED_SUPPORT_THRESHOLD));

        if (support > PROVED_SUPPORT_THRESHOLD && isHighTrust) {
            // RULE 1: Strong evidence from high-trust sources
            System.out.println("      RULE 1 TRIGGERED: Strong evidence from high-trust source");
            if (state.getStatus() == SkillStatus.INFERRED || state.getStatus() == SkillStatus.ACTIVE) {
                System.out.println("      Transition: " + state.getStatus() + " → PROVED");
                state.setStatus(SkillStatus.PROVED);
            } else if (state.getStatus() == SkillStatus.UNSEEN) {
                // High-trust evidence for UNSEEN skill → INFERRED (can't skip to PROVED)
                System.out.println("      Transition: UNSEEN → INFERRED (high-trust evidence, but can't skip to PROVED)");
                state.setStatus(SkillStatus.INFERRED);
            } else {
                System.out.println("      No transition (already at " + state.getStatus() + ")");
            }
            state.setConfidence(Math.max(state.getConfidence(), support));
            state.setEvidenceScore(state.getEvidenceScore() + support * 10);
            state.setLastEvidenceAt(now);
            state.setStaleAt(now.plusDays(decayDays));
        } else if (support > 0.4) {
            // RULE 2: Medium evidence
            System.out.println("      RULE 2 TRIGGERED: Medium evidence (support > 0.4)");
            if (state.getStatus() == SkillStatus.UNSEEN) {
                System.out.println("      Transition: UNSEEN → INFERRED");
                state.setStatus(SkillStatus.INFERRED);
            } else {
                System.out.println("      No transition (already at " + state.getStatus() + ")");
            }
            state.setConfidence(Math.max(state.getConfidence(), support * 0.7));
            state.setEvidenceScore(state.getEvidenceScore() + support * 5);
            state.setLastEvidenceAt(now);
        } else {
            System.out.println("      NO RULE TRIGGERED: Support too low (" + String.format("%.2f", support) + " <= 0.4)");
        }

        state.setUpdatedAt(now);
        UserSkillState savedState = userSkillStateRepo.save(state);

        System.out.println("      SAVED: " + savedState.getStatus() + " (confidence=" + String.format("%.2f", savedState.getConfidence()) + ")");
        System.out.println("  <-- StateTransitionService.updateStateFromEvidence() COMPLETE");
    }

    @Transactional
    public void recomputeFrontierForSkill(Integer userId, Integer skillId) {
        UserSkillState state = userSkillStateRepo.findByUserIdAndSkillId(userId, skillId).orElse(null);
        if (state == null || state.getStatus() == SkillStatus.PROVED) {
            return;
        }

        List<PrereqEdge> prereqs = prereqEdgeRepo.findByToSkillId(skillId);
        if (prereqs.isEmpty()) {
            return;
        }

        List<Integer> prereqSkillIds = prereqs.stream()
                .map(PrereqEdge::getFromSkillId)
                .collect(Collectors.toList());
        List<UserSkillState> prereqStates = userSkillStateRepo.findByUserIdAndSkillIdIn(userId, prereqSkillIds);
        Map<Integer, Double> prereqConfidence = prereqStates.stream()
                .collect(Collectors.toMap(UserSkillState::getSkillId, UserSkillState::getConfidence));

        boolean hardPrereqsSatisfied = true;
        for (PrereqEdge edge : prereqs) {
            if (edge.getType() == PrereqEdge.EdgeType.HARD) {
                double conf = prereqConfidence.getOrDefault(edge.getFromSkillId(), 0.0);
                if (conf < HARD_PREREQ_THRESHOLD) {
                    hardPrereqsSatisfied = false;
                    break;
                }
            }
        }

        if (!hardPrereqsSatisfied) {
            return;
        }

        double aggregateConfidence = 0.0;
        double totalStrength = 0.0;
        for (PrereqEdge edge : prereqs) {
            double conf = prereqConfidence.getOrDefault(edge.getFromSkillId(), 0.0);
            aggregateConfidence += conf * edge.getStrength();
            totalStrength += edge.getStrength();
        }
        double avgConfidence = totalStrength > 0 ? aggregateConfidence / totalStrength : 0.0;

        if (avgConfidence >= SOFT_PREREQ_THRESHOLD && state.getStatus() == SkillStatus.INFERRED) {
            state.setStatus(SkillStatus.ACTIVE);
            state.setUpdatedAt(LocalDateTime.now());
            userSkillStateRepo.save(state);
        }
    }

    @Transactional
    public void recomputeUserFrontier(Integer userId, Integer roleId) {
        List<UserSkillState> allStates = userSkillStateRepo.findByUserId(userId);
        for (UserSkillState state : allStates) {
            if (state.getStatus() != SkillStatus.PROVED) {
                recomputeFrontierForSkill(userId, state.getSkillId());
            }
        }
    }

    @Transactional
    public void runDecayJob() {
        List<UserSkillState> staleStates = userSkillStateRepo.findStaleStates(LocalDateTime.now());
        for (UserSkillState state : staleStates) {
            if (state.getStatus() == SkillStatus.PROVED) {
                state.setStatus(SkillStatus.STALE);
                state.setConfidence(state.getConfidence() * 0.8);
                state.setUpdatedAt(LocalDateTime.now());
                userSkillStateRepo.save(state);
            }
        }
    }

    private boolean isHighTrustEvidence(String type) {
        return "QUIZ".equals(type) || "CERT".equals(type);
    }
}
