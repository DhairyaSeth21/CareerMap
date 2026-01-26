package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements")
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "progress")
    private Integer progress = 0; // For tracking progress towards unlocking

    public UserAchievement() {}

    public UserAchievement(User user, Achievement achievement, Integer progress) {
        this.user = user;
        this.achievement = achievement;
        this.progress = progress;
        this.unlockedAt = null;
    }

    public Integer getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public boolean isUnlocked() {
        return unlockedAt != null;
    }
}
