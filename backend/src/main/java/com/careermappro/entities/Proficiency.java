package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "proficiencies")
public class Proficiency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String skill;

    private Double proficiency;

    public Proficiency() {}

    public Proficiency(Integer userId, String skill, Double proficiency) {
        this.userId = userId;
        this.skill = skill;
        this.proficiency = proficiency;
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public Double getProficiency() { return proficiency; }
    public void setProficiency(Double proficiency) { this.proficiency = proficiency; }
}
