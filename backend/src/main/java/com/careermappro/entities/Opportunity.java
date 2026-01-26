package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "jd_raw_text", columnDefinition = "TEXT", nullable = false)
    private String jdRawText;

    @Column(name = "jd_hash", nullable = false)
    private String jdHash; // SHA-256 hash for idempotency

    @Column(name = "normalized_role")
    private String normalizedRole;

    @Column(name = "extracted_json", columnDefinition = "TEXT")
    private String extractedJson; // JSON of extracted data from OpenAI

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Opportunity() {
        this.createdAt = LocalDateTime.now();
    }

    public Opportunity(User user, String jdRawText, String jdHash) {
        this.user = user;
        this.jdRawText = jdRawText;
        this.jdHash = jdHash;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getJdRawText() { return jdRawText; }
    public void setJdRawText(String jdRawText) { this.jdRawText = jdRawText; }

    public String getJdHash() { return jdHash; }
    public void setJdHash(String jdHash) { this.jdHash = jdHash; }

    public String getNormalizedRole() { return normalizedRole; }
    public void setNormalizedRole(String normalizedRole) { this.normalizedRole = normalizedRole; }

    public String getExtractedJson() { return extractedJson; }
    public void setExtractedJson(String extractedJson) { this.extractedJson = extractedJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
