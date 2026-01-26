package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "deep_path_steps")
public class DeepPathStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Integer stepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deep_path_id", nullable = false)
    private DeepPath deepPath;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillNode skill;

    @Column(name = "order_in_week")
    private Integer orderInWeek = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    public DeepPathStep() {}

    public DeepPathStep(DeepPath deepPath, Integer weekNumber, SkillNode skill, Integer orderInWeek, String description) {
        this.deepPath = deepPath;
        this.weekNumber = weekNumber;
        this.skill = skill;
        this.orderInWeek = orderInWeek;
        this.description = description;
    }

    // Getters and Setters
    public Integer getStepId() { return stepId; }
    public void setStepId(Integer stepId) { this.stepId = stepId; }

    public DeepPath getDeepPath() { return deepPath; }
    public void setDeepPath(DeepPath deepPath) { this.deepPath = deepPath; }

    public Integer getWeekNumber() { return weekNumber; }
    public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }

    public SkillNode getSkill() { return skill; }
    public void setSkill(SkillNode skill) { this.skill = skill; }

    public Integer getOrderInWeek() { return orderInWeek; }
    public void setOrderInWeek(Integer orderInWeek) { this.orderInWeek = orderInWeek; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
