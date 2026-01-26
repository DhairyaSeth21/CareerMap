package com.careermappro.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks user ratings and preferences for learning resources
 * Powers personalized resource recommendations
 */
@Entity
@Table(name = "user_resource_ratings")
@IdClass(UserResourceRatingId.class)
public class UserResourceRating {

    @Id
    @Column(name = "user_id")
    @JsonProperty("userId")
    private Integer userId;

    @Id
    @Column(name = "resource_id")
    @JsonProperty("resourceId")
    private Integer resourceId;

    @Column(name = "rating", nullable = false)
    private Float rating; // 1-5 stars

    @Column(name = "helpful")
    private Boolean helpful = true; // True = "This helped", False = "Find different"

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback; // Optional user comment

    @Column(name = "rated_at")
    @JsonProperty("ratedAt")
    private LocalDateTime ratedAt;

    // Constructors
    public UserResourceRating() {
        this.ratedAt = LocalDateTime.now();
    }

    public UserResourceRating(Integer userId, Integer resourceId, Float rating, Boolean helpful) {
        this();
        this.userId = userId;
        this.resourceId = resourceId;
        this.rating = rating;
        this.helpful = helpful;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Boolean getHelpful() {
        return helpful;
    }

    public void setHelpful(Boolean helpful) {
        this.helpful = helpful;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(LocalDateTime ratedAt) {
        this.ratedAt = ratedAt;
    }

    @Override
    public String toString() {
        return "UserResourceRating{" +
                "userId=" + userId +
                ", resourceId=" + resourceId +
                ", rating=" + rating +
                ", helpful=" + helpful +
                ", ratedAt=" + ratedAt +
                '}';
    }
}
