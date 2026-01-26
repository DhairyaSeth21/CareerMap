package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "career_goals")
public class CareerGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String goal;

    private Double readiness;

    private String skill;

    private Double weight;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    public CareerGoal() {}

    public CareerGoal(Integer userId, String goal, Double readiness) {
        this.userId = userId;
        this.goal = goal;
        this.readiness = readiness;
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public Double getReadiness() { return readiness; }
    public void setReadiness(Double readiness) { this.readiness = readiness; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
}
