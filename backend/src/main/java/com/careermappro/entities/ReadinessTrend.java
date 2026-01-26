package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "readiness_trend")
public class ReadinessTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String domain;

    private Double score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ReadinessTrend() {}

    public ReadinessTrend(Integer userId, String domain, Double score, LocalDateTime createdAt) {
        this.userId = userId;
        this.domain = domain;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
