package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "path_progress")
public class PathProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;

    @Column(name = "path_id", nullable = false)
    private Integer pathId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "current_unit_id")
    private Integer currentUnitId;

    @Column(name = "current_step_id")
    private Integer currentStepId;

    @Column(name = "total_units", nullable = false)
    private Integer totalUnits = 0;

    @Column(name = "completed_units", nullable = false)
    private Integer completedUnits = 0;

    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps = 0;

    @Column(name = "completed_steps", nullable = false)
    private Integer completedSteps = 0;

    @Column(name = "total_time_minutes", nullable = false)
    private Integer totalTimeMinutes = 0;

    @Column(name = "evidence_submitted", nullable = false)
    private Integer evidenceSubmitted = 0;

    @Column(name = "quizzes_completed", nullable = false)
    private Integer quizzesCompleted = 0;

    @Column(name = "overall_progress", precision = 5, scale = 2)
    private BigDecimal overallProgress = new BigDecimal("0.00");

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PathProgress() {
        this.totalUnits = 0;
        this.completedUnits = 0;
        this.totalSteps = 0;
        this.completedSteps = 0;
        this.totalTimeMinutes = 0;
        this.evidenceSubmitted = 0;
        this.quizzesCompleted = 0;
        this.overallProgress = new BigDecimal("0.00");
        this.updatedAt = LocalDateTime.now();
    }

    public PathProgress(Integer pathId, Integer userId) {
        this();
        this.pathId = pathId;
        this.userId = userId;
    }

    // Getters and Setters
    public Integer getProgressId() { return progressId; }
    public void setProgressId(Integer progressId) { this.progressId = progressId; }

    public Integer getPathId() { return pathId; }
    public void setPathId(Integer pathId) { this.pathId = pathId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCurrentUnitId() { return currentUnitId; }
    public void setCurrentUnitId(Integer currentUnitId) { this.currentUnitId = currentUnitId; }

    public Integer getCurrentStepId() { return currentStepId; }
    public void setCurrentStepId(Integer currentStepId) { this.currentStepId = currentStepId; }

    public Integer getTotalUnits() { return totalUnits; }
    public void setTotalUnits(Integer totalUnits) { this.totalUnits = totalUnits; }

    public Integer getCompletedUnits() { return completedUnits; }
    public void setCompletedUnits(Integer completedUnits) { this.completedUnits = completedUnits; }

    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer totalSteps) { this.totalSteps = totalSteps; }

    public Integer getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(Integer completedSteps) { this.completedSteps = completedSteps; }

    public Integer getTotalTimeMinutes() { return totalTimeMinutes; }
    public void setTotalTimeMinutes(Integer totalTimeMinutes) { this.totalTimeMinutes = totalTimeMinutes; }

    public Integer getEvidenceSubmitted() { return evidenceSubmitted; }
    public void setEvidenceSubmitted(Integer evidenceSubmitted) { this.evidenceSubmitted = evidenceSubmitted; }

    public Integer getQuizzesCompleted() { return quizzesCompleted; }
    public void setQuizzesCompleted(Integer quizzesCompleted) { this.quizzesCompleted = quizzesCompleted; }

    public BigDecimal getOverallProgress() { return overallProgress; }
    public void setOverallProgress(BigDecimal overallProgress) { this.overallProgress = overallProgress; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
