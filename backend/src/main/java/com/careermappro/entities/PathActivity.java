package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "path_activities")
public class PathActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Integer activityId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "path_id", nullable = false)
    private Integer pathId;

    @Column(name = "step_id")
    private Integer stepId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ActivityType {
        PATH_STARTED,
        UNIT_UNLOCKED,
        UNIT_COMPLETED,
        STEP_UNLOCKED,
        STEP_STARTED,
        STEP_COMPLETED,
        EVIDENCE_SUBMITTED,
        QUIZ_TAKEN,
        RESOURCE_ACCESSED,
        MILESTONE_REACHED
    }

    public PathActivity() {
        this.createdAt = LocalDateTime.now();
    }

    public PathActivity(Integer userId, Integer pathId, ActivityType activityType) {
        this();
        this.userId = userId;
        this.pathId = pathId;
        this.activityType = activityType;
    }

    public PathActivity(Integer userId, Integer pathId, Integer stepId, ActivityType activityType) {
        this();
        this.userId = userId;
        this.pathId = pathId;
        this.stepId = stepId;
        this.activityType = activityType;
    }

    // Getters and Setters
    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPathId() { return pathId; }
    public void setPathId(Integer pathId) { this.pathId = pathId; }

    public Integer getStepId() { return stepId; }
    public void setStepId(Integer stepId) { this.stepId = stepId; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
