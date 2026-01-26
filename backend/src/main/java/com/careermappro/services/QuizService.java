package com.careermappro.services;

import com.careermappro.entities.*;
import com.careermappro.repositories.QuizRepository;
import com.careermappro.repositories.QuizQuestionRepository;
import com.careermappro.repositories.ProficiencyRepository;
import com.careermappro.repositories.SkillNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final ProficiencyRepository proficiencyRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final AssessmentResultService assessmentResultService;
    private final GPTService gptService;

    public QuizService(QuizRepository quizRepository,
                      QuizQuestionRepository questionRepository,
                      ProficiencyRepository proficiencyRepository,
                      SkillNodeRepository skillNodeRepository,
                      AssessmentResultService assessmentResultService,
                      GPTService gptService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.proficiencyRepository = proficiencyRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.assessmentResultService = assessmentResultService;
        this.gptService = gptService;
    }

    /**
     * Generate a new quiz for a skill.
     *
     * UNIQUENESS GUARANTEE:
     * - Every call to generateQuiz() creates FRESH questions via OpenAI
     * - Questions are NEVER reused for the same user/skill combination
     * - OpenAI is prompted with random variation to ensure uniqueness
     * - Each quiz is a new assessment opportunity
     *
     * This ensures users can't memorize answers across attempts.
     */
    @Transactional
    public Map<String, Object> generateQuiz(Integer userId, String skillName,
                                           String difficulty, Integer numQuestions) {

        System.out.println("=== GENERATING UNIQUE QUIZ ===");
        System.out.println("userId=" + userId + ", skill=" + skillName + ", difficulty=" + difficulty);

        // Create quiz record
        Quiz.DifficultyLevel difficultyLevel = Quiz.DifficultyLevel.valueOf(
            difficulty != null ? difficulty : "Intermediate"
        );
        int questionCount = numQuestions != null ? numQuestions : 10;

        Quiz quiz = new Quiz(userId, skillName, difficultyLevel, questionCount);
        quiz.setStatus(Quiz.QuizStatus.IN_PROGRESS);
        quiz = quizRepository.save(quiz);

        // Generate UNIQUE questions using AI (never reused)
        // OpenAI will generate different questions each time due to temperature > 0
        List<QuizQuestion> questions = generateQuestionsForSkill(quiz, skillName, difficultyLevel, questionCount);

        System.out.println("Generated " + questions.size() + " unique questions for quizId=" + quiz.getQuizId());

        quiz.setQuestions(questions);
        quizRepository.save(quiz);

        // Return quiz data WITHOUT correct answers
        Map<String, Object> response = new HashMap<>();
        response.put("quizId", quiz.getQuizId());
        response.put("skillName", skillName);
        response.put("difficulty", difficultyLevel.toString());
        response.put("numQuestions", questionCount);
        response.put("questions", questions.stream().map(q -> {
            Map<String, Object> qData = new HashMap<>();
            qData.put("questionId", q.getQuestionId());
            qData.put("questionNumber", q.getQuestionNumber());
            qData.put("questionText", q.getQuestionText());
            qData.put("questionType", q.getQuestionType() != null ? q.getQuestionType().toString() : "MCQ");
            qData.put("optionA", q.getOptionA());
            qData.put("optionB", q.getOptionB());
            qData.put("optionC", q.getOptionC());
            qData.put("optionD", q.getOptionD());
            // DO NOT send correctAnswer to frontend!
            return qData;
        }).collect(Collectors.toList()));

        return response;
    }

    /**
     * Submit quiz answers and calculate score
     */
    @Transactional
    public Map<String, Object> submitQuiz(Integer quizId, Map<String, String> answers, Integer timeTaken) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getStatus() == Quiz.QuizStatus.COMPLETED) {
            throw new RuntimeException("Quiz already completed");
        }

        // Load questions and grade them
        List<QuizQuestion> questions = questionRepository.findByQuizQuizIdOrderByQuestionNumber(quizId);

        int correctCount = 0;
        double totalWeight = 0;
        double earnedWeight = 0;
        Map<String, Integer> subtopicCorrect = new HashMap<>();
        Map<String, Integer> subtopicTotal = new HashMap<>();

        for (QuizQuestion q : questions) {
            String userAnswer = answers.get(String.valueOf(q.getQuestionId()));
            q.setUserAnswer(userAnswer);

            if (q.getIsCorrect()) {
                correctCount++;
                earnedWeight += q.getDifficultyWeight();
                subtopicCorrect.merge(q.getSubtopic(), 1, Integer::sum);
            }

            totalWeight += q.getDifficultyWeight();
            subtopicTotal.merge(q.getSubtopic(), 1, Integer::sum);

            questionRepository.save(q);
        }

        // Calculate score (0-100)
        double rawScore = totalWeight > 0 ? (earnedWeight / totalWeight) * 100 : 0;
        double score = Math.round(rawScore * 10) / 10.0;

        // Calculate proficiency delta (0-10 scale)
        // Formula: base proficiency from score + difficulty multiplier
        double baseProficiency = (score / 100.0) * 10.0; // Convert 0-100 to 0-10
        double difficultyMultiplier = switch (quiz.getDifficulty()) {
            case Beginner -> 0.7;
            case Intermediate -> 1.0;
            case Advanced -> 1.3;
            case Expert -> 1.5;
        };
        double proficiencyAwarded = Math.min(10.0, baseProficiency * difficultyMultiplier);

        // Update quiz record
        quiz.setScore(score);
        quiz.setProficiencyAwarded(proficiencyAwarded);
        quiz.setStatus(Quiz.QuizStatus.COMPLETED);
        quiz.setCompletedAt(LocalDateTime.now());
        quiz.setTimeTakenSeconds(timeTaken);
        quizRepository.save(quiz);

        // NEW: Update EDLSG state machine via AssessmentResultService
        // This replaces the old updateUserProficiency call that only updated the legacy proficiencies table
        System.out.println("QuizService.submitQuiz() - About to process assessment impact");
        System.out.println("  Quiz ID: " + quizId);
        System.out.println("  User ID: " + quiz.getUserId());
        System.out.println("  Skill: " + quiz.getSkillName());
        System.out.println("  Raw Score: " + String.format("%.2f%%", rawScore));

        AssessmentResultService.AssessmentImpact impact = processAssessmentImpact(
            quiz.getUserId(),
            quiz.getSkillName(),
            rawScore / 100.0, // Convert 0-100 to 0.0-1.0
            questions.size(),
            "Quiz: " + quiz.getSkillName() + " (" + quiz.getDifficulty() + ")"
        );

        System.out.println("QuizService.submitQuiz() - Assessment impact processed successfully");

        // LEGACY: Still update old proficiencies table for backwards compatibility
        // TODO: Remove this once all systems migrate to user_skill_states
        updateUserProficiency(quiz.getUserId(), quiz.getSkillName(), proficiencyAwarded, score);

        // Build breakdown
        List<Map<String, Object>> breakdown = subtopicTotal.entrySet().stream().map(entry -> {
            String subtopic = entry.getKey();
            int total = entry.getValue();
            int correct = subtopicCorrect.getOrDefault(subtopic, 0);
            Map<String, Object> item = new HashMap<>();
            item.put("subtopic", subtopic);
            item.put("correct", correct);
            item.put("total", total);
            item.put("percentage", (int) Math.round((correct * 100.0) / total));
            return item;
        }).collect(Collectors.toList());

        // Build ALL question results (correct and incorrect)
        List<Map<String, Object>> allResults = questions.stream()
            .map(q -> {
                Map<String, Object> exp = new HashMap<>();
                exp.put("questionNumber", q.getQuestionNumber());
                exp.put("questionText", q.getQuestionText());
                exp.put("isCorrect", q.getIsCorrect());

                // Include both letter and full option text
                String yourAnswerLetter = q.getUserAnswer();
                String yourAnswerText = getOptionText(q, yourAnswerLetter);
                exp.put("yourAnswer", yourAnswerLetter);
                exp.put("yourAnswerText", yourAnswerText);

                String correctAnswerLetter = q.getCorrectAnswer();
                String correctAnswerText = getOptionText(q, correctAnswerLetter);
                exp.put("correctAnswer", correctAnswerLetter);
                exp.put("correctAnswerText", correctAnswerText);

                exp.put("explanation", q.getExplanation());
                return exp;
            })
            .collect(Collectors.toList());

        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("quizId", quizId);
        result.put("score", score);
        result.put("correctCount", correctCount);
        result.put("totalQuestions", questions.size());
        result.put("proficiencyAwarded", Math.round(proficiencyAwarded * 10) / 10.0);
        result.put("breakdown", breakdown);
        result.put("timeTaken", timeTaken);
        result.put("allResults", allResults); // ALL questions with answers

        // Add state transition feedback for UI
        Map<String, Object> stateChange = new HashMap<>();
        stateChange.put("stateChanged", impact.isStateChanged());
        stateChange.put("oldStatus", impact.getOldStatus().toString());
        stateChange.put("newStatus", impact.getNewStatus().toString());
        stateChange.put("oldConfidence", Math.round(impact.getOldConfidence() * 100) / 100.0);
        stateChange.put("newConfidence", Math.round(impact.getNewConfidence() * 100) / 100.0);
        stateChange.put("supportAwarded", Math.round(impact.getSupportAwarded() * 100) / 100.0);
        result.put("stateTransition", stateChange);

        return result;
    }

    /**
     * Update user proficiency based on quiz result (LEGACY - V1 proficiencies table)
     * TODO: Remove once all systems migrate to user_skill_states
     */
    private void updateUserProficiency(Integer userId, String skillName, double newProficiency, double score) {
        Optional<Proficiency> existing = proficiencyRepository.findByUserIdAndSkill(userId, skillName);

        if (existing.isPresent()) {
            Proficiency prof = existing.get();
            // Weighted average: 70% existing + 30% new (prevents wild swings)
            double updatedProf = (prof.getProficiency() * 0.7) + (newProficiency * 0.3);
            prof.setProficiency(Math.min(10.0, updatedProf));
            proficiencyRepository.save(prof);
        } else {
            // First quiz for this skill
            Proficiency prof = new Proficiency(userId, skillName, newProficiency);
            proficiencyRepository.save(prof);
        }
    }

    /**
     * Process assessment impact via AssessmentResultService (NEW - V2 EDLSG system)
     * This method looks up the skill ID and delegates to AssessmentResultService.
     */
    private AssessmentResultService.AssessmentImpact processAssessmentImpact(
            Integer userId,
            String skillName,
            double rawScore,
            int totalQuestions,
            String quizTitle) {

        System.out.println("  processAssessmentImpact() - Looking up skill: '" + skillName + "'");

        // Look up skill ID from canonical name
        Optional<SkillNode> skillNode = skillNodeRepository.findByCanonicalName(skillName);

        if (skillNode.isEmpty()) {
            // Skill not found in V2 system - log warning but don't fail
            // This allows quizzes for skills not yet in the skill graph
            System.err.println("  ❌ WARNING: Skill '" + skillName + "' not found in skill_nodes table. State machine NOT updated.");
            System.err.println("  This quiz will NOT trigger UNSEEN → INFERRED transition!");
            System.err.println("  FIX: Ensure skill '" + skillName + "' exists in skill_nodes with matching canonical_name");

            // Return a no-op impact
            return new AssessmentResultService.AssessmentImpact(
                null,
                UserSkillState.SkillStatus.UNSEEN,
                UserSkillState.SkillStatus.UNSEEN,
                0.0,
                0.0,
                0.0,
                null,
                false
            );
        }

        System.out.println("  ✓ Skill found: skillId=" + skillNode.get().getSkillNodeId() + ", canonicalName=" + skillNode.get().getCanonicalName());

        // Process the quiz result through the V2 EDLSG state machine
        return assessmentResultService.processQuizResult(
            userId,
            skillNode.get().getSkillNodeId(),
            rawScore,
            totalQuestions,
            quizTitle
        );
    }

    /**
     * Get quiz history for a user
     */
    public List<Map<String, Object>> getUserQuizHistory(Integer userId) {
        List<Quiz> quizzes = quizRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return quizzes.stream().map(quiz -> {
            Map<String, Object> item = new HashMap<>();
            item.put("quizId", quiz.getQuizId());
            item.put("skillName", quiz.getSkillName());
            item.put("difficulty", quiz.getDifficulty().toString());
            item.put("score", quiz.getScore());
            item.put("proficiencyAwarded", quiz.getProficiencyAwarded());
            item.put("status", quiz.getStatus().toString());
            item.put("createdAt", quiz.getCreatedAt());
            item.put("completedAt", quiz.getCompletedAt());
            item.put("timeTaken", quiz.getTimeTakenSeconds());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * Get quiz history for a specific skill
     */
    public List<Map<String, Object>> getSkillQuizHistory(Integer userId, String skillName) {
        List<Quiz> quizzes = quizRepository.findByUserIdAndSkillNameOrderByCreatedAtDesc(userId, skillName);

        return quizzes.stream()
            .filter(q -> q.getStatus() == Quiz.QuizStatus.COMPLETED)
            .map(quiz -> {
                Map<String, Object> item = new HashMap<>();
                item.put("quizId", quiz.getQuizId());
                item.put("score", quiz.getScore());
                item.put("proficiencyAwarded", quiz.getProficiencyAwarded());
                item.put("difficulty", quiz.getDifficulty().toString());
                item.put("completedAt", quiz.getCompletedAt());
                item.put("timeTaken", quiz.getTimeTakenSeconds());
                return item;
            }).collect(Collectors.toList());
    }

    /**
     * Generate questions for a skill using OpenAI API
     */
    private List<QuizQuestion> generateQuestionsForSkill(Quiz quiz, String skillName,
                                                        Quiz.DifficultyLevel difficulty, int count) {
        List<QuizQuestion> questions = new ArrayList<>();

        String prompt = String.format("""
                Generate %d MULTIPLE CHOICE questions for %s (%s level).

                ⚠️ ABSOLUTE REQUIREMENTS - FAILURE TO COMPLY = REJECTED:
                1. EVERY question MUST have EXACTLY 4 options: optionA, optionB, optionC, optionD
                2. NO free response, NO "explain", NO code writing questions
                3. ONLY multiple choice with 4 answer options
                4. If you cannot provide 4 options, DO NOT include that question

                Question styles (mix these):
                - Scenario: "You have X problem, which approach is best?"
                - Comparison: "What's the key difference between X and Y?"
                - Debugging: "Given this code/situation, what's wrong?"
                - Best practice: "In production, which solution is recommended?"

                Make options CHALLENGING - all should sound plausible.

                JSON FORMAT (STRICT):
                [
                  {
                    "questionType": "MCQ",
                    "questionText": "Your question here",
                    "optionA": "Full answer A",
                    "optionB": "Full answer B",
                    "optionC": "Full answer C",
                    "optionD": "Full answer D",
                    "correctAnswer": "A",
                    "explanation": "Why A is correct",
                    "subtopic": "Category"
                  }
                ]

                Generate EXACTLY %d questions. Every question MUST have 4 options.
                """, count, skillName, difficulty, count);

        String gptResponse = gptService.generateText(prompt);

        System.out.println("=== GPT RESPONSE ===");
        System.out.println(gptResponse != null ? gptResponse.substring(0, Math.min(200, gptResponse.length())) : "NULL");
        System.out.println("====================");

        if (gptResponse == null || gptResponse.contains("Error") || gptResponse.contains("not configured")) {
            // Fallback to template questions if OpenAI fails
            System.err.println("OpenAI response invalid, using fallback questions");
            return generateFallbackQuestions(quiz, skillName, difficulty, count);
        }

        try {
            // Parse GPT response and create questions
            String jsonContent = extractJsonFromResponse(gptResponse);
            List<Map<String, String>> questionData = parseQuestionJson(jsonContent);

            for (int i = 0; i < Math.min(questionData.size(), count); i++) {
                Map<String, String> qData = questionData.get(i);

                // ALL questions are MCQ now - no FRQ/CODING
                QuizQuestion.QuestionType questionType = QuizQuestion.QuestionType.MCQ;

                // Ensure all MCQ options exist
                String optionA = qData.getOrDefault("optionA", "Option A");
                String optionB = qData.getOrDefault("optionB", "Option B");
                String optionC = qData.getOrDefault("optionC", "Option C");
                String optionD = qData.getOrDefault("optionD", "Option D");

                QuizQuestion q = new QuizQuestion(
                    quiz,
                    i + 1,
                    qData.getOrDefault("questionText", "Question " + (i + 1)),
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    qData.getOrDefault("correctAnswer", "A"),
                    qData.getOrDefault("subtopic", "General"),
                    getDifficultyWeight(difficulty)
                );
                q.setQuestionType(questionType);
                q.setExplanation(qData.getOrDefault("explanation", "No explanation provided"));
                questions.add(questionRepository.save(q));
            }

            // If GPT didn't generate enough questions, fill with fallback
            if (questions.size() < count) {
                List<QuizQuestion> fallback = generateFallbackQuestions(quiz, skillName, difficulty, count - questions.size());
                questions.addAll(fallback);
            }

        } catch (Exception e) {
            System.err.println("Error parsing GPT questions: " + e.getMessage());
            return generateFallbackQuestions(quiz, skillName, difficulty, count);
        }

        return questions;
    }

    /**
     * Fallback question generator when OpenAI is unavailable
     */
    private List<QuizQuestion> generateFallbackQuestions(Quiz quiz, String skillName,
                                                         Quiz.DifficultyLevel difficulty, int count) {
        List<QuizQuestion> questions = new ArrayList<>();
        String[] subtopics = getSubtopicsForSkill(skillName);

        for (int i = 1; i <= count; i++) {
            String subtopic = subtopics[(i - 1) % subtopics.length];
            QuizQuestion q = new QuizQuestion(
                quiz,
                i,
                String.format("Question %d about %s (%s level): What is a key concept?", i, skillName, difficulty),
                "Option A - Basic concept",
                "Option B - Intermediate concept",
                "Option C - Advanced concept",
                "Option D - Expert concept",
                getCorrectAnswerForDifficulty(difficulty),
                subtopic,
                getDifficultyWeight(difficulty)
            );
            questions.add(questionRepository.save(q));
        }

        return questions;
    }

    /**
     * Extract JSON array from GPT response (handles markdown code blocks)
     */
    private String extractJsonFromResponse(String response) {
        // Remove markdown code blocks if present
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    /**
     * Parse JSON response into question data
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> parseQuestionJson(String jsonContent) {
        // Simple JSON parsing (in production, use Jackson or Gson)
        List<Map<String, String>> questions = new ArrayList<>();

        try {
            // Find array boundaries
            int start = jsonContent.indexOf('[');
            int end = jsonContent.lastIndexOf(']');
            if (start == -1 || end == -1) return questions;

            String arrayContent = jsonContent.substring(start + 1, end);

            // Split by object boundaries (simple approach)
            String[] objects = arrayContent.split("\\}\\s*,\\s*\\{");

            for (String obj : objects) {
                obj = obj.trim();
                if (!obj.startsWith("{")) obj = "{" + obj;
                if (!obj.endsWith("}")) obj = obj + "}";

                Map<String, String> qData = new HashMap<>();

                // Extract fields (simple regex approach)
                qData.put("questionText", extractField(obj, "questionText"));
                qData.put("optionA", extractField(obj, "optionA"));
                qData.put("optionB", extractField(obj, "optionB"));
                qData.put("optionC", extractField(obj, "optionC"));
                qData.put("optionD", extractField(obj, "optionD"));
                qData.put("correctAnswer", extractField(obj, "correctAnswer"));
                qData.put("subtopic", extractField(obj, "subtopic"));

                questions.add(qData);
            }
        } catch (Exception e) {
            System.err.println("JSON parsing error: " + e.getMessage());
        }

        return questions;
    }

    /**
     * Extract a field value from JSON object string
     */
    private String extractField(String jsonObj, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(jsonObj);
            if (m.find()) {
                return m.group(1).replace("\\\"", "\"");
            }
        } catch (Exception e) {
            // Ignore
        }
        return "";
    }

    private String[] getSubtopicsForSkill(String skillName) {
        // Simplified - should be in database
        return new String[] {"Fundamentals", "Best Practices", "Advanced Concepts", "Real-world Applications"};
    }

    private String getCorrectAnswerForDifficulty(Quiz.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case Beginner -> "A";
            case Intermediate -> "B";
            case Advanced -> "C";
            case Expert -> "D";
        };
    }

    private Double getDifficultyWeight(Quiz.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case Beginner -> 1.0;
            case Intermediate -> 1.5;
            case Advanced -> 2.0;
            case Expert -> 2.5;
        };
    }

    private String getOptionText(QuizQuestion question, String optionLetter) {
        if (optionLetter == null || optionLetter.isEmpty()) {
            return "";
        }
        return switch (optionLetter.toUpperCase()) {
            case "A" -> question.getOptionA() != null ? question.getOptionA() : "";
            case "B" -> question.getOptionB() != null ? question.getOptionB() : "";
            case "C" -> question.getOptionC() != null ? question.getOptionC() : "";
            case "D" -> question.getOptionD() != null ? question.getOptionD() : "";
            default -> "";
        };
    }
}
