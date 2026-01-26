package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_dependencies")
public class SkillDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependency_id")
    private Integer dependencyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_skill_id", nullable = false)
    private Skill prerequisiteSkill;

    @Enumerated(EnumType.STRING)
    @Column(name = "dependency_type")
    private DependencyType dependencyType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum DependencyType {
        required, recommended, related
    }

    public SkillDependency() {}

    public SkillDependency(Skill skill, Skill prerequisiteSkill, DependencyType dependencyType) {
        this.skill = skill;
        this.prerequisiteSkill = prerequisiteSkill;
        this.dependencyType = dependencyType;
    }

    // Getters and Setters
    public Integer getDependencyId() { return dependencyId; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Skill getPrerequisiteSkill() { return prerequisiteSkill; }
    public void setPrerequisiteSkill(Skill prerequisiteSkill) { this.prerequisiteSkill = prerequisiteSkill; }

    public DependencyType getDependencyType() { return dependencyType; }
    public void setDependencyType(DependencyType dependencyType) { this.dependencyType = dependencyType; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
