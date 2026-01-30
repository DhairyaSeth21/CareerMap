package com.careermappro.controllers;

import com.careermappro.entities.CareerRole;
import com.careermappro.entities.Session;
import com.careermappro.entities.SkillNode;
import com.careermappro.models.DetailedPathNode;
import com.careermappro.repositories.CareerRoleRepository;
import com.careermappro.repositories.SkillNodeRepository;
import com.careermappro.services.OpenAIService;
import com.careermappro.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * CoreLoopController
 * Implements the CORE LOOP: Select Role → Generate Path → Focus Node → PROBE → Grade → Explain → Update → Next
 */
@RestController
@RequestMapping("/api/core-loop")
public class CoreLoopController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CareerRoleRepository careerRoleRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    /**
     * STEP 1: Select a role and generate AI-powered learning path
     * POST /api/core-loop/select-role
     */
    @PostMapping("/select-role")
    public ResponseEntity<?> selectRole(
        @RequestParam Integer userId,
        @RequestParam Integer roleId
    ) {
        try {
            // Get role
            CareerRole role = careerRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            // Get all available skills for this role's domain
            List<SkillNode> availableSkills = skillNodeRepository.findAll(); // TODO: Filter by domain

            // Convert to format for OpenAI
            List<Map<String, Object>> skillsForAI = new ArrayList<>();
            for (SkillNode skill : availableSkills) {
                Map<String, Object> skillMap = new HashMap<>();
                skillMap.put("id", skill.getSkillNodeId());
                skillMap.put("name", skill.getCanonicalName());
                skillMap.put("difficulty", skill.getDifficulty());
                skillsForAI.add(skillMap);
            }

            // Generate personalized path with OpenAI
            System.out.println(String.format("[CORE LOOP] User %d selected role %s - Generating AI path...",
                userId, role.getName()));
            List<Integer> pathSkillIds = openAIService.generateLearningPath(
                roleId, role.getName(), userId, skillsForAI
            );

            // Get focus node (first skill in path)
            Integer focusNodeId = pathSkillIds.isEmpty() ? availableSkills.get(0).getSkillNodeId() : pathSkillIds.get(0);
            SkillNode focusNode = skillNodeRepository.findById(focusNodeId).orElseThrow();

            // Propose PROBE session for focus node
            Session session = sessionService.proposeProbeSession(userId, focusNodeId);

            Map<String, Object> response = new HashMap<>();
            response.put("role", Map.of("id", role.getCareerRoleId(), "name", role.getName()));
            response.put("pathSkillIds", pathSkillIds);
            response.put("focusNode", Map.of(
                "id", focusNode.getSkillNodeId(),
                "name", focusNode.getCanonicalName(),
                "difficulty", focusNode.getDifficulty()
            ));
            response.put("session", Map.of(
                "id", session.getSessionId(),
                "type", session.getSessionType(),
                "state", session.getSessionState()
            ));

            System.out.println(String.format("[CORE LOOP] Path generated with %d skills. Focus: %s (Session %d)",
                pathSkillIds.size(), focusNode.getCanonicalName(), session.getSessionId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * STEP 1B: Get DETAILED learning path with resources and dependencies
     * POST /api/core-loop/generate-detailed-path
     */
    @PostMapping("/generate-detailed-path")
    public ResponseEntity<?> generateDetailedPath(
        @RequestParam Integer userId,
        @RequestParam Integer roleId
    ) {
        try {
            // Get role
            CareerRole role = careerRoleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            // Get ROLE-SPECIFIC skills (not all skills!)
            List<SkillNode> availableSkills = skillNodeRepository.findSkillsByRoleId(roleId);

            // Fallback: if no role-specific skills found, use all skills
            if (availableSkills.isEmpty()) {
                System.out.println("[CORE LOOP] WARNING: No role-specific skills found for role " + roleId + ", using all skills");
                availableSkills = skillNodeRepository.findAll();
            } else {
                System.out.println("[CORE LOOP] Found " + availableSkills.size() + " role-specific skills for " + role.getName());
            }

            // Convert to format for OpenAI
            List<Map<String, Object>> skillsForAI = new ArrayList<>();
            for (SkillNode skill : availableSkills) {
                Map<String, Object> skillMap = new HashMap<>();
                skillMap.put("id", skill.getSkillNodeId());
                skillMap.put("name", skill.getCanonicalName());
                skillMap.put("difficulty", skill.getDifficulty());
                skillsForAI.add(skillMap);
            }

            // Generate DETAILED personalized path with OpenAI
            System.out.println(String.format("[CORE LOOP] User %d requested detailed path for role %s - Generating...",
                userId, role.getName()));
            List<DetailedPathNode> detailedPath = openAIService.generateDetailedLearningPath(
                roleId, role.getName(), userId, skillsForAI
            );

            // Get focus node (first skill in path)
            DetailedPathNode focusNode = detailedPath.isEmpty() ? null : detailedPath.get(0);

            // Propose PROBE session for focus node if exists
            // Don't fail the entire request if session creation fails
            // (skill nodes may not exist in database for template-based paths)
            Session session = null;
            if (focusNode != null) {
                try {
                    session = sessionService.proposeProbeSession(userId, focusNode.getSkillNodeId());
                } catch (Exception e) {
                    System.out.println("[CORE LOOP] Skipping session creation - " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("role", Map.of("id", role.getCareerRoleId(), "name", role.getName()));
            response.put("path", detailedPath);
            response.put("focusNode", focusNode);
            if (session != null) {
                response.put("session", Map.of(
                    "id", session.getSessionId(),
                    "type", session.getSessionType(),
                    "state", session.getSessionState()
                ));
            }

            System.out.println(String.format("[CORE LOOP] Detailed path generated with %d nodes. Focus: %s",
                detailedPath.size(), focusNode != null ? focusNode.getName() : "none"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * STEP 2: Start PROBE session and get fresh AI-generated quiz
     * POST /api/core-loop/start-probe
     */
    @PostMapping("/start-probe")
    public ResponseEntity<?> startProbe(@RequestParam Integer sessionId) {
        try {
            // Start session (now idempotent - returns session if already ACTIVE)
            Session session = sessionService.startSession(sessionId);
            SkillNode skill = session.getSkillNode();

            // Generate fresh quiz with OpenAI
            System.out.println(String.format("[CORE LOOP] Starting PROBE for skill '%s' - Generating fresh quiz...",
                skill.getCanonicalName()));

            Map<String, Object> quiz = openAIService.generateQuiz(
                skill.getCanonicalName(),
                String.valueOf(skill.getDifficulty()),
                5 // 5 questions per probe
            );

            Map<String, Object> response = new HashMap<>();
            response.put("session", Map.of(
                "id", session.getSessionId(),
                "state", session.getSessionState(),
                "skillName", skill.getCanonicalName()
            ));
            response.put("quiz", quiz);

            System.out.println(String.format("[CORE LOOP] Quiz generated with %d questions",
                ((List<?>)quiz.get("questions")).size()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * STEP 3: Submit quiz answers, grade, and get AI explanations for wrong answers
     * POST /api/core-loop/submit-quiz
     */
    @PostMapping("/submit-quiz")
    public ResponseEntity<?> submitQuiz(
        @RequestParam Integer sessionId,
        @RequestBody Map<String, Object> submission
    ) {
        try {
            Session session = sessionService.startSession(sessionId); // Ensure it's active
            SkillNode skill = session.getSkillNode();

            @SuppressWarnings("unchecked")
            List<Map<String, String>> answers = (List<Map<String, String>>) submission.get("answers");

            // Grade the quiz
            int correct = 0;
            List<Map<String, Object>> gradedAnswers = new ArrayList<>();

            for (Map<String, String> answer : answers) {
                String userAnswer = answer.get("userAnswer");
                String correctAnswer = answer.get("correctAnswer");
                String question = answer.get("question");

                boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
                if (isCorrect) correct++;

                Map<String, Object> gradedAnswer = new HashMap<>();
                gradedAnswer.put("question", question);
                gradedAnswer.put("userAnswer", userAnswer);
                gradedAnswer.put("correctAnswer", correctAnswer);
                gradedAnswer.put("isCorrect", isCorrect);

                // Generate AI explanation for wrong answers
                if (!isCorrect) {
                    System.out.println(String.format("[CORE LOOP] Generating explanation for wrong answer..."));
                    String explanation = openAIService.generateExplanation(
                        question, correctAnswer, userAnswer, skill.getCanonicalName()
                    );
                    gradedAnswer.put("explanation", explanation);
                } else {
                    gradedAnswer.put("explanation", "Correct!");
                }

                gradedAnswers.add(gradedAnswer);
            }

            // Calculate score
            BigDecimal score = BigDecimal.valueOf((double) correct / answers.size());

            // Complete session (updates state and confidence)
            Session completedSession = sessionService.completeSession(sessionId, score, null);

            Map<String, Object> response = new HashMap<>();
            response.put("score", score);
            response.put("correct", correct);
            response.put("total", answers.size());
            response.put("gradedAnswers", gradedAnswers);
            response.put("confidenceChange", Map.of(
                "before", completedSession.getConfidenceBefore(),
                "after", completedSession.getConfidenceAfter()
            ));

            System.out.println(String.format("[CORE LOOP] Quiz graded: %d/%d (%.0f%%) - Confidence: %.2f → %.2f",
                correct, answers.size(), score.doubleValue() * 100,
                completedSession.getConfidenceBefore(), completedSession.getConfidenceAfter()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get next focus node (for continuing the learning path)
     * GET /api/core-loop/next-focus
     */
    @GetMapping("/next-focus")
    public ResponseEntity<?> getNextFocus(
        @RequestParam Integer userId,
        @RequestParam List<Integer> completedSkillIds
    ) {
        // TODO: Implement logic to determine next skill based on path and completed skills
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Next focus calculation not yet implemented");
        return ResponseEntity.ok(response);
    }
}
