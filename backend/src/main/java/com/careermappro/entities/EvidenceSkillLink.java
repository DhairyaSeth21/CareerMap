package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_skill_links")
public class EvidenceSkillLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Integer linkId;

    @Column(name = "evidence_id", nullable = false)
    private Integer evidenceId;

    @Column(name = "skill_id", nullable = false)
    private Integer skillId;

    @Column(name = "support", nullable = false)
    private Double support; // 0.0 to 1.0

    @Column(name = "extracted_by", length = 50)
    private String extractedBy; // e.g., "gpt-4", "manual"

    @Column(name = "confidence")
    private Double confidence; // LLM's confidence in the extraction

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public EvidenceSkillLink() {
        this.createdAt = LocalDateTime.now();
    }

    public EvidenceSkillLink(Integer evidenceId, Integer skillId, Double support, String extractedBy) {
        this.evidenceId = evidenceId;
        this.skillId = skillId;
        this.support = support;
        this.extractedBy = extractedBy;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public Integer getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Integer evidenceId) {
        this.evidenceId = evidenceId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public Double getSupport() {
        return support;
    }

    public void setSupport(Double support) {
        this.support = support;
    }

    public String getExtractedBy() {
        return extractedBy;
    }

    public void setExtractedBy(String extractedBy) {
        this.extractedBy = extractedBy;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
