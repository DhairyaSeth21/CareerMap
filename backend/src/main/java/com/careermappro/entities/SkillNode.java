package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_nodes")
public class SkillNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_node_id")
    private Integer skillNodeId;

    @Column(name = "canonical_name", unique = true, nullable = false)
    private String canonicalName;

    @Column(name = "aliases", columnDefinition = "JSON")
    private String aliases;

    @Column(name = "domain")
    private String domain;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "decay_half_life_days")
    private Integer decayHalfLifeDays;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public SkillNode() {
        this.createdAt = LocalDateTime.now();
        this.difficulty = 3;
        this.decayHalfLifeDays = 180;
        this.aliases = "[]";
    }

    public Integer getSkillNodeId() { return skillNodeId; }
    public void setSkillNodeId(Integer skillNodeId) { this.skillNodeId = skillNodeId; }
    public String getCanonicalName() { return canonicalName; }
    public void setCanonicalName(String canonicalName) { this.canonicalName = canonicalName; }
    public String getAliases() { return aliases; }
    public void setAliases(String aliases) { this.aliases = aliases; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public Integer getDecayHalfLifeDays() { return decayHalfLifeDays; }
    public void setDecayHalfLifeDays(Integer decayHalfLifeDays) { this.decayHalfLifeDays = decayHalfLifeDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
