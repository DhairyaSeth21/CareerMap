package com.careermappro.controllers;

import com.careermappro.services.AIExplanationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AIExplanationController
 * Provides AI-powered explanations for skill nodes using OpenAI
 */
@RestController
@RequestMapping("/api/ai")
public class AIExplanationController {

    @Autowired
    private AIExplanationService aiExplanationService;

    /**
     * Generate AI explanation for a skill node
     * POST /api/ai/explain
     * Body: { skillName, whyItMatters, proofRequirement, learnResources[] }
     */
    @PostMapping("/explain")
    public ResponseEntity<?> explainSkillNode(@RequestBody Map<String, Object> skillData) {
        try {
            Map<String, Object> explanation = aiExplanationService.generateExplanation(skillData);
            return ResponseEntity.ok(explanation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate explanation: " + e.getMessage()));
        }
    }
}
