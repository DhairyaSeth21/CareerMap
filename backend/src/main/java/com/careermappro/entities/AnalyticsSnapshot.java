package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_snapshots")
public class AnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private Double readiness;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AnalyticsSnapshot() {}

    public AnalyticsSnapshot(Integer userId, Double readiness, LocalDateTime createdAt) {
        this.userId = userId;
        this.readiness = readiness;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Double getReadiness() { return readiness; }
    public void setReadiness(Double readiness) { this.readiness = readiness; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
