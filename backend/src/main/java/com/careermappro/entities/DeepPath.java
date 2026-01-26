package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deep_paths")
public class DeepPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deep_path_id")
    private Integer deepPathId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_role_id", nullable = false)
    private CareerRole careerRole;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_weeks")
    private Integer durationWeeks = 12;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "deepPath", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("week_number ASC, order_in_week ASC")
    private List<DeepPathStep> steps = new ArrayList<>();

    public DeepPath() {}

    public DeepPath(CareerRole careerRole, String name, String description, Integer durationWeeks) {
        this.careerRole = careerRole;
        this.name = name;
        this.description = description;
        this.durationWeeks = durationWeeks;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getDeepPathId() { return deepPathId; }
    public void setDeepPathId(Integer deepPathId) { this.deepPathId = deepPathId; }

    public CareerRole getCareerRole() { return careerRole; }
    public void setCareerRole(CareerRole careerRole) { this.careerRole = careerRole; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<DeepPathStep> getSteps() { return steps; }
    public void setSteps(List<DeepPathStep> steps) { this.steps = steps; }
}
