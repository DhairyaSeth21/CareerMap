package com.careermappro.models;

/**
 * LearningResource
 * Represents a specific learning material (article, video, course) with actual URL
 */
public class LearningResource {
    private String type; // "article", "video", "course", "documentation"
    private String title;
    private String url; // ACTUAL WORKING URL
    private String description;
    private Integer estimatedMinutes;

    public LearningResource() {}

    public LearningResource(String type, String title, String url, String description, Integer estimatedMinutes) {
        this.type = type;
        this.title = title;
        this.url = url;
        this.description = description;
        this.estimatedMinutes = estimatedMinutes;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
