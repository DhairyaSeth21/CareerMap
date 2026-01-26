package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "study_resources")
public class StudyResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Integer resourceId;

    @Column(name = "step_id", nullable = false)
    private Integer stepId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ResourceType type;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "author", length = 200)
    private String author;

    @Column(name = "relevance_score", precision = 3, scale = 2)
    private BigDecimal relevanceScore = new BigDecimal("0.80");

    @Column(name = "generated_by", length = 50)
    private String generatedBy = "gpt-4o-mini";

    @Column(name = "is_helpful")
    private Boolean isHelpful;

    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ResourceType {
        ARTICLE, VIDEO, TUTORIAL, DOCUMENTATION, COURSE, PRACTICE
    }

    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    public StudyResource() {
        this.createdAt = LocalDateTime.now();
        this.relevanceScore = new BigDecimal("0.80");
        this.generatedBy = "gpt-4o-mini";
    }

    public StudyResource(Integer stepId, ResourceType type, String title, String url, Difficulty difficulty) {
        this();
        this.stepId = stepId;
        this.type = type;
        this.title = title;
        this.url = url;
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public Integer getResourceId() { return resourceId; }
    public void setResourceId(Integer resourceId) { this.resourceId = resourceId; }

    public Integer getStepId() { return stepId; }
    public void setStepId(Integer stepId) { this.stepId = stepId; }

    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public Boolean getIsHelpful() { return isHelpful; }
    public void setIsHelpful(Boolean isHelpful) { this.isHelpful = isHelpful; }

    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
