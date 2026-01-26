package com.careermappro.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "career_roles")
public class CareerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_role_id")
    private Integer careerRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    @JsonBackReference
    private Domain domain;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String icon;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "careerRole", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DeepPath> deepPaths = new HashSet<>();

    public CareerRole() {}

    public CareerRole(Domain domain, String name, String description, String icon) {
        this.domain = domain;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getCareerRoleId() { return careerRoleId; }
    public void setCareerRoleId(Integer careerRoleId) { this.careerRoleId = careerRoleId; }

    public Domain getDomain() { return domain; }
    public void setDomain(Domain domain) { this.domain = domain; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Set<DeepPath> getDeepPaths() { return deepPaths; }
    public void setDeepPaths(Set<DeepPath> deepPaths) { this.deepPaths = deepPaths; }
}
