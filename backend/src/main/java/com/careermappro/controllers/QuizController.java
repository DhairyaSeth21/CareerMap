package com.careermappro.controllers;

import com.careermappro.services.QuizService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * POST /api/quizzes/generate
     * Generate a new quiz for a skill
     * Body: { userId, skillName, difficulty?, numQuestions? }
     */
    @PostMapping("/generate")
    public Map<String, Object> generateQuiz(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String skillName = (String) request.get("skillName");
        String difficulty = (String) request.getOrDefault("difficulty", "Intermediate");
        Integer numQuestions = request.containsKey("numQuestions")
            ? (Integer) request.get("numQuestions")
            : 10;

        return quizService.generateQuiz(userId, skillName, difficulty, numQuestions);
    }

    /**
     * POST /api/quizzes/{quizId}/submit
     * Submit quiz answers
     * Body: { answers: { "questionId": "A", ... }, timeTaken: 180 }
     */
    @PostMapping("/{quizId}/submit")
    public Map<String, Object> submitQuiz(
            @PathVariable Integer quizId,
            @RequestBody Map<String, Object> request) {

        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) request.get("answers");
        Integer timeTaken = (Integer) request.getOrDefault("timeTaken", 0);

        return quizService.submitQuiz(quizId, answers, timeTaken);
    }

    /**
     * GET /api/quizzes/user/{userId}
     * Get all quizzes for a user
     */
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserQuizzes(@PathVariable Integer userId) {
        return quizService.getUserQuizHistory(userId);
    }

    /**
     * GET /api/quizzes/user/{userId}/skill/{skillName}
     * Get quiz history for a specific skill
     */
    @GetMapping("/user/{userId}/skill/{skillName}")
    public List<Map<String, Object>> getSkillQuizHistory(
            @PathVariable Integer userId,
            @PathVariable String skillName) {
        return quizService.getSkillQuizHistory(userId, skillName);
    }
}
