package com.careermappro.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Session Entity
 * Represents a focused learning activity (PROBE/BUILD/PROVE/APPLY)
 * Core of the session-based engagement model
 */
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_node_id", nullable = false)
    private SkillNode skillNode;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_state", nullable = false)
    private SessionState sessionState = SessionState.PROPOSED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(name = "score")
    private BigDecimal score;

    @Column(name = "confidence_before")
    private BigDecimal confidenceBefore;

    @Column(name = "confidence_after")
    private BigDecimal confidenceAfter;

    public Session() {
        this.createdAt = LocalDateTime.now();
        this.sessionState = SessionState.PROPOSED;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // Set expiration to 24 hours for PROPOSED sessions
        if (sessionState == SessionState.PROPOSED && expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(24);
        }
    }

    // Enums
    public enum SessionType {
        PROBE,   // Assessment-based learning (quizzes)
        BUILD,   // Project-based learning
        PROVE,   // Evidence submission
        APPLY    // Real-world application
    }

    public enum SessionState {
        PROPOSED,  // System proposed, user not started
        ACTIVE,    // User started, in progress
        COMPLETED, // User finished successfully
        EXPIRED    // Time limit passed
    }

    // Helper methods for state transitions with logging
    public void startSession() {
        if (sessionState != SessionState.PROPOSED) {
            throw new IllegalStateException("Can only start PROPOSED sessions");
        }
        this.sessionState = SessionState.ACTIVE;
        this.startedAt = LocalDateTime.now();
        System.out.println(String.format("[SESSION] User %d started %s session for skill %d",
            userId, sessionType, skillNode.getSkillNodeId()));
    }

    public void completeSession(BigDecimal score, BigDecimal confidenceAfter) {
        if (sessionState != SessionState.ACTIVE) {
            throw new IllegalStateException("Can only complete ACTIVE sessions");
        }
        this.sessionState = SessionState.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.score = score;
        this.confidenceAfter = confidenceAfter;
        System.out.println(String.format("[SESSION] User %d completed %s session for skill %d - Score: %.2f, Confidence: %.2f → %.2f",
            userId, sessionType, skillNode.getSkillNodeId(), score, confidenceBefore, confidenceAfter));
    }

    public void expireSession() {
        if (sessionState == SessionState.COMPLETED) {
            throw new IllegalStateException("Cannot expire COMPLETED sessions");
        }
        this.sessionState = SessionState.EXPIRED;
        System.out.println(String.format("[SESSION] Session %d for user %d expired",
            sessionId, userId));
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // Getters and Setters
    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public SkillNode getSkillNode() { return skillNode; }
    public void setSkillNode(SkillNode skillNode) { this.skillNode = skillNode; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public SessionState getSessionState() { return sessionState; }
    public void setSessionState(SessionState sessionState) { this.sessionState = sessionState; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public BigDecimal getConfidenceBefore() { return confidenceBefore; }
    public void setConfidenceBefore(BigDecimal confidenceBefore) { this.confidenceBefore = confidenceBefore; }

    public BigDecimal getConfidenceAfter() { return confidenceAfter; }
    public void setConfidenceAfter(BigDecimal confidenceAfter) { this.confidenceAfter = confidenceAfter; }
}
