package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "path_steps")
public class PathStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Integer stepId;

    @Column(name = "unit_id", nullable = false)
    private Integer unitId;

    @Column(name = "skill_id", nullable = false)
    private Integer skillId;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(name = "success_criteria", columnDefinition = "TEXT")
    private String successCriteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status = StepStatus.LOCKED;

    @Column(name = "confidence_target", precision = 3, scale = 2)
    private BigDecimal confidenceTarget = new BigDecimal("0.70");

    @Column(name = "evidence_count")
    private Integer evidenceCount = 0;

    @Column(name = "attempts")
    private Integer attempts = 0;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum StepStatus {
        LOCKED, AVAILABLE, IN_PROGRESS, COMPLETED
    }

    public PathStep() {
        this.status = StepStatus.LOCKED;
        this.confidenceTarget = new BigDecimal("0.70");
        this.evidenceCount = 0;
        this.attempts = 0;
    }

    public PathStep(Integer unitId, Integer skillId, Integer stepNumber, String title) {
        this();
        this.unitId = unitId;
        this.skillId = skillId;
        this.stepNumber = stepNumber;
        this.title = title;
    }

    // Getters and Setters
    public Integer getStepId() { return stepId; }
    public void setStepId(Integer stepId) { this.stepId = stepId; }

    public Integer getUnitId() { return unitId; }
    public void setUnitId(Integer unitId) { this.unitId = unitId; }

    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLearningObjectives() { return learningObjectives; }
    public void setLearningObjectives(String learningObjectives) { this.learningObjectives = learningObjectives; }

    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }

    public StepStatus getStatus() { return status; }
    public void setStatus(StepStatus status) { this.status = status; }

    public BigDecimal getConfidenceTarget() { return confidenceTarget; }
    public void setConfidenceTarget(BigDecimal confidenceTarget) { this.confidenceTarget = confidenceTarget; }

    public Integer getEvidenceCount() { return evidenceCount; }
    public void setEvidenceCount(Integer evidenceCount) { this.evidenceCount = evidenceCount; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
