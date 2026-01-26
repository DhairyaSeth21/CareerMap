package com.careermappro.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a curated learning resource (video, article, documentation, etc.)
 * Resources are dynamically selected and personalized per user
 */
@Entity
@Table(name = "curated_resources")
public class CuratedResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    @JsonProperty("resourceId")
    private Integer resourceId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "type", length = 50)
    private String type; // video, article, documentation, course, interactive

    @Column(name = "source", length = 100)
    private String source; // official_docs, mdn, youtube, freecodecamp, etc.

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_minutes")
    @JsonProperty("estimatedMinutes")
    private Integer estimatedMinutes;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags; // JSON array: ["sql", "postgresql", "joins"]

    @Column(name = "avg_quality_score")
    @JsonProperty("avgQualityScore")
    private Float avgQualityScore = 3.0f;

    @Column(name = "total_ratings")
    @JsonProperty("totalRatings")
    private Integer totalRatings = 0;

    @Column(name = "last_verified")
    @JsonProperty("lastVerified")
    private LocalDateTime lastVerified;

    @Column(name = "created_at", updatable = false)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Transient field - not stored in DB, computed per user
    @Transient
    @JsonProperty("personalizedScore")
    private Float personalizedScore;

    @Transient
    @JsonProperty("userRating")
    private Float userRating;

    // Constructors
    public CuratedResource() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastVerified = LocalDateTime.now();
    }

    public CuratedResource(String title, String url, String type, String source, String description, Integer estimatedMinutes) {
        this();
        this.title = title;
        this.url = url;
        this.type = type;
        this.source = source;
        this.description = description;
        this.estimatedMinutes = estimatedMinutes;
    }

    // Getters and Setters
    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Float getAvgQualityScore() {
        return avgQualityScore;
    }

    public void setAvgQualityScore(Float avgQualityScore) {
        this.avgQualityScore = avgQualityScore;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public LocalDateTime getLastVerified() {
        return lastVerified;
    }

    public void setLastVerified(LocalDateTime lastVerified) {
        this.lastVerified = lastVerified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Float getPersonalizedScore() {
        return personalizedScore;
    }

    public void setPersonalizedScore(Float personalizedScore) {
        this.personalizedScore = personalizedScore;
    }

    public Float getUserRating() {
        return userRating;
    }

    public void setUserRating(Float userRating) {
        this.userRating = userRating;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Parse tags from JSON string to List
     */
    public List<String> getTagsList() {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(tags, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String toString() {
        return "CuratedResource{" +
                "resourceId=" + resourceId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", avgQualityScore=" + avgQualityScore +
                ", totalRatings=" + totalRatings +
                '}';
    }
}
