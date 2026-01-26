package com.careermappro.services;

import com.careermappro.entities.Session;
import com.careermappro.entities.Session.SessionState;
import com.careermappro.entities.Session.SessionType;
import com.careermappro.entities.SkillNode;
import com.careermappro.entities.UserSkillState;
import com.careermappro.repositories.SessionRepository;
import com.careermappro.repositories.SkillNodeRepository;
import com.careermappro.repositories.UserSkillStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * SessionService
 * Manages the lifecycle of learning sessions (PROBE/BUILD/PROVE/APPLY)
 * Implements strict state transitions with logging
 */
@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    @Autowired
    private UserSkillStateRepository userSkillStateRepository;

    /**
     * Propose a new PROBE session for a user on a specific skill
     * Only ONE session can be PROPOSED at a time (enforces focus)
     */
    @Transactional
    public Session proposeProbeSession(Integer userId, Integer skillNodeId) {
        // Check if user already has a PROPOSED session for this skill - return it if not expired
        Optional<Session> existingProposed = sessionRepository.findFirstByUserIdAndSessionStateOrderByCreatedAtDesc(
            userId, SessionState.PROPOSED
        );

        if (existingProposed.isPresent()) {
            Session proposed = existingProposed.get();
            // If expired, expire it and continue to create new session
            if (proposed.isExpired()) {
                proposed.expireSession();
                sessionRepository.save(proposed);
            } else if (proposed.getSkillNode().getSkillNodeId().equals(skillNodeId)) {
                return proposed; // Return existing valid session for same skill
            } else {
                throw new IllegalStateException("User already has a PROPOSED session for a different skill. Complete or expire it first.");
            }
        }

        // Check if user has an ACTIVE session for this skill - return it if not expired
        Optional<Session> existingActive = sessionRepository.findFirstByUserIdAndSessionStateOrderByCreatedAtDesc(
            userId, SessionState.ACTIVE
        );

        if (existingActive.isPresent()) {
            Session active = existingActive.get();
            // If expired, expire it and continue to create new session
            if (active.isExpired()) {
                active.expireSession();
                sessionRepository.save(active);
            } else if (active.getSkillNode().getSkillNodeId().equals(skillNodeId)) {
                return active; // Return existing valid active session for same skill
            }
        }

        // Get the skill node
        SkillNode skillNode = skillNodeRepository.findById(skillNodeId)
            .orElseThrow(() -> new IllegalArgumentException("Skill node not found: " + skillNodeId));

        // Get current confidence (if exists)
        BigDecimal currentConfidence = BigDecimal.ZERO;
        Optional<UserSkillState> userSkillState = userSkillStateRepository.findByUserIdAndSkillId(userId, skillNodeId);
        if (userSkillState.isPresent()) {
            currentConfidence = BigDecimal.valueOf(userSkillState.get().getConfidence());
        }

        // Create session
        Session session = new Session();
        session.setUserId(userId);
        session.setSkillNode(skillNode);
        session.setSessionType(SessionType.PROBE);
        session.setSessionState(SessionState.PROPOSED);
        session.setConfidenceBefore(currentConfidence);
        session.setExpiresAt(LocalDateTime.now().plusHours(24));

        Session saved = sessionRepository.save(session);

        System.out.println(String.format("[SESSION] PROPOSED PROBE session %d for user %d on skill '%s' (confidence: %.2f)",
            saved.getSessionId(), userId, skillNode.getCanonicalName(), currentConfidence));

        return saved;
    }

    /**
     * Start a proposed session
     */
    @Transactional
    public Session startSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (session.isExpired()) {
            session.expireSession();
            sessionRepository.save(session);
            throw new IllegalStateException("Session has expired");
        }

        // If already ACTIVE, just return it (idempotent operation)
        if (session.getSessionState() == SessionState.ACTIVE) {
            return session;
        }

        session.startSession();
        return sessionRepository.save(session);
    }

    /**
     * Complete a session with score and updated confidence
     * This is where the CRITICAL state transition happens: UNSEEN → INFERRED → ACTIVE → PROVED
     */
    @Transactional
    public Session completeSession(Integer sessionId, BigDecimal score, Integer quizId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (session.getSessionState() != SessionState.ACTIVE) {
            throw new IllegalStateException("Can only complete ACTIVE sessions");
        }

        // Calculate new confidence based on score
        BigDecimal newConfidence = calculateNewConfidence(
            session.getConfidenceBefore(),
            score,
            session.getSessionType()
        );

        // Update session
        session.setQuizId(quizId);
        session.completeSession(score, newConfidence);
        Session saved = sessionRepository.save(session);

        // Update user skill state (THIS IS WHERE THE BUG WAS - UNSEEN → INFERRED)
        updateUserSkillState(session.getUserId(), session.getSkillNode().getSkillNodeId(), newConfidence, score);

        return saved;
    }

    /**
     * Update user skill state after session completion
     * CRITICAL: Fixes UNSEEN → INFERRED bug with proper state transitions
     */
    private void updateUserSkillState(Integer userId, Integer skillNodeId, BigDecimal newConfidence, BigDecimal score) {
        Optional<UserSkillState> existing = userSkillStateRepository.findByUserIdAndSkillId(userId, skillNodeId);

        if (existing.isPresent()) {
            UserSkillState state = existing.get();
            UserSkillState.SkillStatus oldStatus = state.getStatus();
            Double oldConfidence = state.getConfidence();

            // Update confidence
            state.setConfidence(newConfidence.doubleValue());
            state.setUpdatedAt(LocalDateTime.now());

            // State transition logic (FIXED)
            UserSkillState.SkillStatus newStatus = determineNewStatus(state.getStatus(), newConfidence, score);
            state.setStatus(newStatus);

            userSkillStateRepository.save(state);

            System.out.println(String.format("[STATE TRANSITION] User %d, Skill %d: %s (%.2f) → %s (%.2f) | Score: %.2f",
                userId, skillNodeId, oldStatus, oldConfidence, newStatus, newConfidence.doubleValue(), score.doubleValue()));
        } else {
            // Create new state (UNSEEN → INFERRED/ACTIVE)
            UserSkillState newState = new UserSkillState();
            newState.setUserId(userId);
            newState.setSkillId(skillNodeId);
            newState.setConfidence(newConfidence.doubleValue());

            // Determine initial state based on performance
            UserSkillState.SkillStatus initialStatus = newConfidence.compareTo(BigDecimal.valueOf(0.7)) >= 0
                ? UserSkillState.SkillStatus.ACTIVE
                : UserSkillState.SkillStatus.INFERRED;
            newState.setStatus(initialStatus);
            newState.setUpdatedAt(LocalDateTime.now());

            userSkillStateRepository.save(newState);

            System.out.println(String.format("[STATE TRANSITION] User %d, Skill %d: UNSEEN → %s (%.2f) | Score: %.2f",
                userId, skillNodeId, initialStatus, newConfidence.doubleValue(), score.doubleValue()));
        }
    }

    /**
     * Determine new state based on current state, confidence, and score
     * FIXED: Proper UNSEEN → INFERRED → ACTIVE → PROVED transitions
     */
    private UserSkillState.SkillStatus determineNewStatus(UserSkillState.SkillStatus currentStatus, BigDecimal confidence, BigDecimal score) {
        // High performance (80%+) with high confidence (70%+) = PROVED
        if (score.compareTo(BigDecimal.valueOf(0.8)) >= 0 &&
            confidence.compareTo(BigDecimal.valueOf(0.7)) >= 0) {
            return UserSkillState.SkillStatus.PROVED;
        }

        // Good performance (60%+) with decent confidence (50%+) = ACTIVE
        if (score.compareTo(BigDecimal.valueOf(0.6)) >= 0 &&
            confidence.compareTo(BigDecimal.valueOf(0.5)) >= 0) {
            return UserSkillState.SkillStatus.ACTIVE;
        }

        // Otherwise = INFERRED (needs more work)
        return UserSkillState.SkillStatus.INFERRED;
    }

    /**
     * Calculate new confidence using Bayesian update
     * Higher weight for assessment scores vs evidence
     */
    private BigDecimal calculateNewConfidence(BigDecimal oldConfidence, BigDecimal score, SessionType sessionType) {
        // Weight based on session type
        double weight = switch (sessionType) {
            case PROBE -> 0.4;  // Assessments get 40% weight
            case PROVE -> 0.2;  // Evidence gets 20% weight (harsher)
            case BUILD -> 0.3;  // Projects get 30% weight
            case APPLY -> 0.5;  // Real application gets 50% weight
        };

        // Bayesian update: new = old + weight * (score - old)
        double oldConf = oldConfidence.doubleValue();
        double newConf = oldConf + weight * (score.doubleValue() - oldConf);

        // Clamp to [0, 1]
        newConf = Math.max(0.0, Math.min(1.0, newConf));

        return BigDecimal.valueOf(newConf);
    }

    /**
     * Get current proposed session for user
     */
    public Optional<Session> getCurrentProposedSession(Integer userId) {
        return sessionRepository.findFirstByUserIdAndSessionStateOrderByCreatedAtDesc(userId, SessionState.PROPOSED);
    }

    /**
     * Get all sessions for user
     */
    public List<Session> getUserSessions(Integer userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Expire old sessions (cleanup job)
     */
    @Transactional
    public void expireOldSessions() {
        List<Session> expiredSessions = sessionRepository.findBySessionStateAndExpiresAtBefore(
            SessionState.PROPOSED,
            LocalDateTime.now()
        );

        for (Session session : expiredSessions) {
            session.expireSession();
            sessionRepository.save(session);
        }

        if (!expiredSessions.isEmpty()) {
            System.out.println(String.format("[SESSION] Expired %d old sessions", expiredSessions.size()));
        }
    }
}
