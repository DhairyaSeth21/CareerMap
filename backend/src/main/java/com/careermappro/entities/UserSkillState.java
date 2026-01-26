package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_skill_states")
public class UserSkillState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "skill_id", nullable = false)
    private Integer skillId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SkillStatus status;

    @Column(name = "confidence", nullable = false)
    private Double confidence = 0.0;

    @Column(name = "evidence_score")
    private Double evidenceScore = 0.0;

    @Column(name = "last_evidence_at")
    private LocalDateTime lastEvidenceAt;

    @Column(name = "stale_at")
    private LocalDateTime staleAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SkillStatus {
        UNSEEN, INFERRED, ACTIVE, PROVED, STALE
    }

    public UserSkillState() {
        this.status = SkillStatus.UNSEEN;
        this.confidence = 0.0;
        this.evidenceScore = 0.0;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }

    public SkillStatus getStatus() { return status; }
    public void setStatus(SkillStatus status) { this.status = status; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Double getEvidenceScore() { return evidenceScore; }
    public void setEvidenceScore(Double evidenceScore) { this.evidenceScore = evidenceScore; }

    public LocalDateTime getLastEvidenceAt() { return lastEvidenceAt; }
    public void setLastEvidenceAt(LocalDateTime lastEvidenceAt) { this.lastEvidenceAt = lastEvidenceAt; }

    public LocalDateTime getStaleAt() { return staleAt; }
    public void setStaleAt(LocalDateTime staleAt) { this.staleAt = staleAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
