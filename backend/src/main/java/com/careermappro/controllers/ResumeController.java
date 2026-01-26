package com.careermappro.controllers;

import com.careermappro.services.ResumeAnalysisService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
public class ResumeController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    /**
     * POST /api/v1/resume/analyze
     * Upload and analyze resume using OpenAI
     * Returns: { skills: [...], matchedNodes: { roleId: [nodeIds] } }
     */
    @PostMapping("/api/v1/resume/analyze")
    public Map<String, Object> analyzeResume(
            @RequestParam Integer userId,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String resumeText
    ) {
        if (file != null) {
            return resumeAnalysisService.analyzeResumeFile(userId, file);
        } else if (resumeText != null && !resumeText.isEmpty()) {
            return resumeAnalysisService.analyzeResumeText(userId, resumeText);
        } else {
            return Map.of("error", "Either file or resumeText must be provided");
        }
    }

    /**
     * POST /api/v1/resume/mark-skills
     * Mark skills as completed based on resume analysis
     * Body: { userId, matchedNodes: { roleId: [nodeIds] } }
     */
    @PostMapping("/api/v1/resume/mark-skills")
    public Map<String, Object> markSkillsFromResume(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        @SuppressWarnings("unchecked")
        Map<Integer, Object> matchedNodes = (Map<Integer, Object>) request.get("matchedNodes");

        return resumeAnalysisService.markSkillsAsCompleted(userId, matchedNodes);
    }
}
