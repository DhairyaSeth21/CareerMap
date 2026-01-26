package com.careermappro.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "option_a", length = 500)
    private String optionA;

    @Column(name = "option_b", length = 500)
    private String optionB;

    @Column(name = "option_c", length = 500)
    private String optionC;

    @Column(name = "option_d", length = 500)
    private String optionD;

    @Column(name = "correct_answer", length = 1, nullable = false)
    private String correctAnswer; // A, B, C, or D

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation; // Explanation for why the correct answer is correct

    @Column(name = "user_answer", length = 500)
    private String userAnswer; // User's answer (can be full text for FRQ)

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "subtopic", length = 100)
    private String subtopic; // For breakdown analysis

    @Column(name = "difficulty_weight")
    private Double difficultyWeight; // 1.0 = easy, 1.5 = medium, 2.0 = hard

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 20)
    private QuestionType questionType; // MCQ, FRQ, CODING

    public enum QuestionType {
        MCQ,    // Multiple Choice Question
        FRQ,    // Free Response Question
        CODING  // Coding Challenge
    }

    public QuizQuestion() {
        this.questionType = QuestionType.MCQ; // Default to MCQ
    }

    public QuizQuestion(Quiz quiz, Integer questionNumber, String questionText,
                       String optionA, String optionB, String optionC, String optionD,
                       String correctAnswer, String subtopic, Double difficultyWeight) {
        this.quiz = quiz;
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.subtopic = subtopic;
        this.difficultyWeight = difficultyWeight;
    }

    // Getters and Setters
    public Integer getQuestionId() { return questionId; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Integer getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(Integer questionNumber) { this.questionNumber = questionNumber; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;

        // Evaluate based on question type
        if (questionType == null || questionType == QuestionType.MCQ) {
            // MCQ: Exact match (case-insensitive)
            this.isCorrect = (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer));
        } else if (questionType == QuestionType.FRQ || questionType == QuestionType.CODING) {
            // FRQ/CODING: Keyword-based or similarity check
            // For now, check if user answer contains key parts of correct answer (lenient grading)
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                this.isCorrect = false;
            } else {
                // Simple keyword matching - check if answer contains at least 50% of key words
                String[] correctKeywords = correctAnswer.toLowerCase().split("\\s+");
                String userAnswerLower = userAnswer.toLowerCase();

                int matchCount = 0;
                for (String keyword : correctKeywords) {
                    if (keyword.length() > 3 && userAnswerLower.contains(keyword)) {
                        matchCount++;
                    }
                }

                // Accept if at least 50% of keywords match
                this.isCorrect = (correctKeywords.length > 0 &&
                                 (double) matchCount / correctKeywords.length >= 0.5);
            }
        }
    }

    public Boolean getIsCorrect() { return isCorrect; }

    public String getSubtopic() { return subtopic; }
    public void setSubtopic(String subtopic) { this.subtopic = subtopic; }

    public Double getDifficultyWeight() { return difficultyWeight; }
    public void setDifficultyWeight(Double difficultyWeight) { this.difficultyWeight = difficultyWeight; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
}
