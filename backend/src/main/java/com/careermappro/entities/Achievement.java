package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private String icon;
    private Integer xpReward;
    private String category; // goal, quiz, streak, skill

    @Column(name = "unlock_criteria")
    private String unlockCriteria; // JSON string or simple count

    public Achievement() {}

    public Achievement(String name, String description, String icon, Integer xpReward, String category, String unlockCriteria) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.xpReward = xpReward;
        this.category = category;
        this.unlockCriteria = unlockCriteria;
    }

    public Integer getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnlockCriteria() { return unlockCriteria; }
    public void setUnlockCriteria(String unlockCriteria) { this.unlockCriteria = unlockCriteria; }
}
