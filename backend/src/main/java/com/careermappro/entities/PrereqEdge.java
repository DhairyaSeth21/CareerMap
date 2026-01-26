package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prereq_edges")
public class PrereqEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edge_id")
    private Integer edgeId;

    @Column(name = "from_skill_id", nullable = false)
    private Integer fromSkillId;

    @Column(name = "to_skill_id", nullable = false)
    private Integer toSkillId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EdgeType type;

    @Column(name = "strength", nullable = false)
    private Double strength;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum EdgeType { HARD, SOFT }

    public PrereqEdge() {
        this.type = EdgeType.SOFT;
        this.strength = 0.70;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getEdgeId() { return edgeId; }
    public void setEdgeId(Integer edgeId) { this.edgeId = edgeId; }
    public Integer getFromSkillId() { return fromSkillId; }
    public void setFromSkillId(Integer fromSkillId) { this.fromSkillId = fromSkillId; }
    public Integer getToSkillId() { return toSkillId; }
    public void setToSkillId(Integer toSkillId) { this.toSkillId = toSkillId; }
    public EdgeType getType() { return type; }
    public void setType(EdgeType type) { this.type = type; }
    public Double getStrength() { return strength; }
    public void setStrength(Double strength) { this.strength = strength; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
