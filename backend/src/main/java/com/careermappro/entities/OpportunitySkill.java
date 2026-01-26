package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "opportunity_skill")
public class OpportunitySkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "importance")
    private String importance; // MUST or NICE

    @Column(name = "evidence_excerpt", columnDefinition = "TEXT")
    private String evidenceExcerpt; // Quote from JD showing this skill

    public OpportunitySkill() {}

    public OpportunitySkill(Opportunity opportunity, Skill skill, String importance, String evidenceExcerpt) {
        this.opportunity = opportunity;
        this.skill = skill;
        this.importance = importance;
        this.evidenceExcerpt = evidenceExcerpt;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public String getImportance() { return importance; }
    public void setImportance(String importance) { this.importance = importance; }

    public String getEvidenceExcerpt() { return evidenceExcerpt; }
    public void setEvidenceExcerpt(String evidenceExcerpt) { this.evidenceExcerpt = evidenceExcerpt; }
}
