package com.careermappro.services;

import com.careermappro.entities.Evidence;
import com.careermappro.entities.EvidenceSkillLink;
import com.careermappro.entities.UserSkillState;
import com.careermappro.repositories.EvidenceRepository;
import com.careermappro.repositories.EvidenceSkillLinkRepository;
import com.careermappro.repositories.UserSkillStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AssessmentResultService bridges quiz/assessment results with the EDLSG state machine.
 *
 * This service fixes the critical bug where quiz completion didn't update user_skill_states.
 * It applies strict scoring curves, auto-creates evidence records, and triggers state transitions.
 *
 * Flow:
 * 1. Quiz completed → processQuizResult()
 * 2. Apply strict scoring curve (make PROVED harder)
 * 3. Create evidence record (type=QUIZ)
 * 4. Link evidence to skill with support score
 * 5. Trigger state machine update via StateTransitionService
 * 6. Return impact summary for UI feedback
 */
@Service
public class AssessmentResultService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceSkillLinkRepository evidenceSkillLinkRepository;
    private final StateTransitionService stateTransitionService;
    private final UserSkillStateRepository userSkillStateRepository;

    // Strict scoring thresholds - make PROVED status harder to achieve
    private static final double PERFECT_SCORE_THRESHOLD = 0.95;  // 95%+ for full confidence
    private static final double STRONG_SCORE_THRESHOLD = 0.85;   // 85-95% for strong support
    private static final double PASSING_SCORE_THRESHOLD = 0.70;  // 70-85% for medium support
    private static final double MIN_SCORE_THRESHOLD = 0.50;      // Below 70% minimal support

    public AssessmentResultService(
            EvidenceRepository evidenceRepository,
            EvidenceSkillLinkRepository evidenceSkillLinkRepository,
            StateTransitionService stateTransitionService,
            UserSkillStateRepository userSkillStateRepository) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceSkillLinkRepository = evidenceSkillLinkRepository;
        this.stateTransitionService = stateTransitionService;
        this.userSkillStateRepository = userSkillStateRepository;
    }

    /**
     * Process quiz result and update skill state machine.
     * This is the main entry point called after quiz submission.
     *
     * @param userId User who took the quiz
     * @param skillId Skill being assessed
     * @param rawScore Raw score (0.0 to 1.0)
     * @param totalQuestions Number of questions in quiz
     * @param quizTitle Title of quiz for evidence description
     * @return Impact summary for UI feedback
     */
    @Transactional
    public AssessmentImpact processQuizResult(
            Integer userId,
            Integer skillId,
            double rawScore,
            int totalQuestions,
            String quizTitle) {

        System.out.println("========================================");
        System.out.println("AssessmentResultService.processQuizResult() CALLED");
        System.out.println("  userId: " + userId);
        System.out.println("  skillId: " + skillId);
        System.out.println("  rawScore: " + String.format("%.2f%%", rawScore * 100));
        System.out.println("  totalQuestions: " + totalQuestions);
        System.out.println("  quizTitle: " + quizTitle);

        // Capture state BEFORE processing
        UserSkillState.SkillStatus oldStatus = getCurrentStatus(userId, skillId);
        double oldConfidence = getCurrentConfidence(userId, skillId);

        System.out.println("  OLD STATE: " + oldStatus + " (confidence: " + String.format("%.2f", oldConfidence) + ")");

        // 1. Apply strict scoring curve
        double adjustedSupport = applyStrictScoringCurve(rawScore);
        System.out.println("  SCORING CURVE: rawScore " + String.format("%.2f%%", rawScore * 100) + " → support " + String.format("%.2f", adjustedSupport));

        // 2. Auto-create evidence record
        Evidence evidence = createEvidenceFromQuiz(userId, rawScore, totalQuestions, quizTitle);
        System.out.println("  EVIDENCE CREATED: evidenceId=" + evidence.getEvidenceId());

        // 3. Link evidence to skill with support score
        createSkillLink(evidence.getEvidenceId(), skillId, adjustedSupport);
        System.out.println("  EVIDENCE LINKED: evidenceId=" + evidence.getEvidenceId() + " → skillId=" + skillId + " (support=" + String.format("%.2f", adjustedSupport) + ")");

        // 4. Trigger state machine update
        System.out.println("  CALLING StateTransitionService.updateStateFromEvidence()...");
        stateTransitionService.updateStateFromEvidence(userId, skillId, adjustedSupport, "QUIZ");

        // 5. Recompute frontier for this skill (check if prereqs unlock new skills)
        System.out.println("  CALLING StateTransitionService.recomputeFrontierForSkill()...");
        stateTransitionService.recomputeFrontierForSkill(userId, skillId);

        // Capture state AFTER processing
        UserSkillState.SkillStatus newStatus = getCurrentStatus(userId, skillId);
        double newConfidence = getCurrentConfidence(userId, skillId);

        System.out.println("  NEW STATE: " + newStatus + " (confidence: " + String.format("%.2f", newConfidence) + ")");
        System.out.println("  STATE CHANGED: " + (oldStatus != newStatus));
        System.out.println("========================================");

        // 6. Build impact summary for UI
        return new AssessmentImpact(
                skillId,
                oldStatus,
                newStatus,
                oldConfidence,
                newConfidence,
                adjustedSupport,
                evidence.getEvidenceId(),
                didStateChange(oldStatus, newStatus)
        );
    }

    /**
     * Apply strict scoring curve to make PROVED status harder to achieve.
     *
     * Curve design:
     * - 95-100%: support = 0.85-1.0 (can trigger PROVED with high-trust evidence)
     * - 85-95%: support = 0.70-0.85 (threshold for PROVED, may require prereqs)
     * - 70-85%: support = 0.50-0.70 (medium support, ACTIVE/INFERRED likely)
     * - 50-70%: support = 0.30-0.50 (weak support, INFERRED likely)
     * - <50%: support = 0.0-0.30 (minimal support, may not change state)
     *
     * This is NOT linear - we penalize scores below 85% more aggressively.
     */
    private double applyStrictScoringCurve(double rawScore) {
        if (rawScore >= PERFECT_SCORE_THRESHOLD) {
            // Perfect score: 95-100% → support 0.85-1.0
            double excess = rawScore - PERFECT_SCORE_THRESHOLD;
            double range = 1.0 - PERFECT_SCORE_THRESHOLD;
            return 0.85 + (excess / range) * 0.15;
        } else if (rawScore >= STRONG_SCORE_THRESHOLD) {
            // Strong score: 85-95% → support 0.70-0.85 (linear)
            double excess = rawScore - STRONG_SCORE_THRESHOLD;
            double range = PERFECT_SCORE_THRESHOLD - STRONG_SCORE_THRESHOLD;
            return 0.70 + (excess / range) * 0.15;
        } else if (rawScore >= PASSING_SCORE_THRESHOLD) {
            // Passing score: 70-85% → support 0.50-0.70
            double excess = rawScore - PASSING_SCORE_THRESHOLD;
            double range = STRONG_SCORE_THRESHOLD - PASSING_SCORE_THRESHOLD;
            return 0.50 + (excess / range) * 0.20;
        } else if (rawScore >= MIN_SCORE_THRESHOLD) {
            // Weak score: 50-70% → support 0.30-0.50
            double excess = rawScore - MIN_SCORE_THRESHOLD;
            double range = PASSING_SCORE_THRESHOLD - MIN_SCORE_THRESHOLD;
            return 0.30 + (excess / range) * 0.20;
        } else {
            // Poor score: <50% → support 0.0-0.30 (harsh penalty)
            return rawScore * 0.6; // Max 0.30 at 50%
        }
    }

    /**
     * Create evidence record from quiz results.
     * Evidence type = QUIZ, rawText contains quiz metadata.
     */
    private Evidence createEvidenceFromQuiz(
            Integer userId,
            double rawScore,
            int totalQuestions,
            String quizTitle) {

        int correctAnswers = (int) Math.round(rawScore * totalQuestions);

        String rawText = String.format(
                "Quiz: %s | Score: %d/%d (%.1f%%) | Completed: %s",
                quizTitle,
                correctAnswers,
                totalQuestions,
                rawScore * 100,
                LocalDateTime.now().toString()
        );

        Evidence evidence = new Evidence(userId, Evidence.EvidenceType.QUIZ, rawText);
        evidence.setSourceUri("quiz://assessment/" + quizTitle.toLowerCase().replaceAll("\\s+", "-"));

        return evidenceRepository.save(evidence);
    }

    /**
     * Create evidence-skill link with calculated support score.
     * extractedBy = "assessment-service" (not LLM, this is deterministic)
     */
    private EvidenceSkillLink createSkillLink(
            Integer evidenceId,
            Integer skillId,
            double support) {

        EvidenceSkillLink link = new EvidenceSkillLink(
                evidenceId,
                skillId,
                support,
                "assessment-service"
        );

        // Confidence = 1.0 (we're certain about the link, unlike LLM extractions)
        link.setConfidence(1.0);

        return evidenceSkillLinkRepository.save(link);
    }

    /**
     * Get current skill status for user (for before/after comparison)
     */
    private UserSkillState.SkillStatus getCurrentStatus(Integer userId, Integer skillId) {
        return userSkillStateRepository.findByUserIdAndSkillId(userId, skillId)
                .map(UserSkillState::getStatus)
                .orElse(UserSkillState.SkillStatus.UNSEEN);
    }

    /**
     * Get current confidence for user (for before/after comparison)
     */
    private double getCurrentConfidence(Integer userId, Integer skillId) {
        return userSkillStateRepository.findByUserIdAndSkillId(userId, skillId)
                .map(UserSkillState::getConfidence)
                .orElse(0.0);
    }

    /**
     * Check if state actually changed (for UI feedback)
     */
    private boolean didStateChange(UserSkillState.SkillStatus oldStatus, UserSkillState.SkillStatus newStatus) {
        return oldStatus != newStatus;
    }

    /**
     * DTO for assessment impact (returned to controller for UI feedback)
     */
    public static class AssessmentImpact {
        private final Integer skillId;
        private final UserSkillState.SkillStatus oldStatus;
        private final UserSkillState.SkillStatus newStatus;
        private final double oldConfidence;
        private final double newConfidence;
        private final double supportAwarded;
        private final Integer evidenceId;
        private final boolean stateChanged;

        public AssessmentImpact(
                Integer skillId,
                UserSkillState.SkillStatus oldStatus,
                UserSkillState.SkillStatus newStatus,
                double oldConfidence,
                double newConfidence,
                double supportAwarded,
                Integer evidenceId,
                boolean stateChanged) {
            this.skillId = skillId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.oldConfidence = oldConfidence;
            this.newConfidence = newConfidence;
            this.supportAwarded = supportAwarded;
            this.evidenceId = evidenceId;
            this.stateChanged = stateChanged;
        }

        // Getters
        public Integer getSkillId() { return skillId; }
        public UserSkillState.SkillStatus getOldStatus() { return oldStatus; }
        public UserSkillState.SkillStatus getNewStatus() { return newStatus; }
        public double getOldConfidence() { return oldConfidence; }
        public double getNewConfidence() { return newConfidence; }
        public double getSupportAwarded() { return supportAwarded; }
        public Integer getEvidenceId() { return evidenceId; }
        public boolean isStateChanged() { return stateChanged; }
    }
}
