package com.careermappro.models;

import java.util.ArrayList;
import java.util.List;

/**
 * DetailedPathNode
 * Represents a single node in an AI-generated learning path with:
 * - Atomic, testable competencies
 * - Working learning resources (actual URLs)
 * - Dependencies and prerequisites
 * - Assessment requirements
 * - Teacher-level guidance
 */
public class DetailedPathNode {
    private Integer skillNodeId; // Links to SkillNode entity
    private String name; // Atomic competency (e.g., "Explain symmetric vs asymmetric encryption")
    private String whyItMatters; // Context and importance
    private List<LearningResource> learnResources; // ACTUAL WORKING URLs
    private String assessmentType; // "probe", "build", "prove", "apply"
    private String proofRequirement; // What the user must demonstrate
    private List<Integer> dependencies; // Skill IDs that must be completed first
    private List<Integer> unlocks; // Skill IDs that become available after completing this
    private List<Integer> competencies; // Related skills that branch out (not prerequisites)
    private Integer difficulty; // 1-10 scale
    private Integer estimatedHours; // Time to complete
    private String category; // "foundational", "core", "advanced", "specialized"

    public DetailedPathNode() {
        this.learnResources = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.unlocks = new ArrayList<>();
        this.competencies = new ArrayList<>();
    }

    // Getters and Setters
    public Integer getSkillNodeId() {
        return skillNodeId;
    }

    public void setSkillNodeId(Integer skillNodeId) {
        this.skillNodeId = skillNodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public List<LearningResource> getLearnResources() {
        return learnResources;
    }

    public void setLearnResources(List<LearningResource> learnResources) {
        this.learnResources = learnResources;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getProofRequirement() {
        return proofRequirement;
    }

    public void setProofRequirement(String proofRequirement) {
        this.proofRequirement = proofRequirement;
    }

    public List<Integer> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Integer> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Integer> getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(List<Integer> unlocks) {
        this.unlocks = unlocks;
    }

    public List<Integer> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(List<Integer> competencies) {
        this.competencies = competencies;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
