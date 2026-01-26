package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role_skill")
public class RoleSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "weight")
    private Double weight = 1.0; // Importance weight for this skill in this role

    @Column(name = "required_level")
    private Double requiredLevel = 7.0; // Target proficiency level (0-10)

    public RoleSkill() {}

    public RoleSkill(Role role, Skill skill, Double weight, Double requiredLevel) {
        this.role = role;
        this.skill = skill;
        this.weight = weight;
        this.requiredLevel = requiredLevel;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getRequiredLevel() { return requiredLevel; }
    public void setRequiredLevel(Double requiredLevel) { this.requiredLevel = requiredLevel; }
}
