package com.careermappro.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private DifficultyLevel difficulty;

    @Column(name = "num_questions", nullable = false)
    private Integer numQuestions;

    @Column(name = "score")
    private Double score; // 0-100

    @Column(name = "proficiency_awarded")
    private Double proficiencyAwarded; // 0-10

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuizStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizQuestion> questions = new ArrayList<>();

    public enum DifficultyLevel {
        Beginner, Intermediate, Advanced, Expert
    }

    public enum QuizStatus {
        PENDING, IN_PROGRESS, COMPLETED, ABANDONED
    }

    public Quiz() {}

    public Quiz(Integer userId, String skillName, DifficultyLevel difficulty, Integer numQuestions) {
        this.userId = userId;
        this.skillName = skillName;
        this.difficulty = difficulty;
        this.numQuestions = numQuestions;
        this.status = QuizStatus.PENDING;
    }

    // Getters and Setters
    public Integer getQuizId() { return quizId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public Integer getNumQuestions() { return numQuestions; }
    public void setNumQuestions(Integer numQuestions) { this.numQuestions = numQuestions; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getProficiencyAwarded() { return proficiencyAwarded; }
    public void setProficiencyAwarded(Double proficiencyAwarded) { this.proficiencyAwarded = proficiencyAwarded; }

    public QuizStatus getStatus() { return status; }
    public void setStatus(QuizStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }

    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
