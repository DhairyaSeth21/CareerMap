package com.careermappro.controllers;

import com.careermappro.services.EvidenceService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/evidence")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    /**
     * POST /api/evidence/project
     * Submit PROJECT evidence with STRICT validation.
     *
     * Body: {
     *   userId: number,
     *   skillId: number,
     *   description: string,
     *   githubUrl?: string,
     *   codeSample?: string
     * }
     */
    @PostMapping("/project")
    public Map<String, Object> submitProjectEvidence(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        Integer skillId = (Integer) request.get("skillId");
        String description = (String) request.get("description");
        String githubUrl = (String) request.get("githubUrl");
        String codeSample = (String) request.get("codeSample");

        EvidenceService.EvidenceSubmissionResult result = evidenceService.submitProjectEvidence(
            userId, skillId, description, githubUrl, codeSample
        );

        return Map.of(
            "success", result.isSuccess(),
            "message", result.getMessage(),
            "evidenceId", result.getEvidenceId() != null ? result.getEvidenceId() : 0,
            "support", result.getSupport()
        );
    }

    /**
     * POST /api/evidence/cert
     * Submit CERT evidence.
     *
     * Body: {
     *   userId: number,
     *   skillId: number,
     *   certName: string,
     *   certUrl?: string,
     *   issuer?: string
     * }
     */
    @PostMapping("/cert")
    public Map<String, Object> submitCertEvidence(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        Integer skillId = (Integer) request.get("skillId");
        String certName = (String) request.get("certName");
        String certUrl = (String) request.get("certUrl");
        String issuer = (String) request.get("issuer");

        EvidenceService.EvidenceSubmissionResult result = evidenceService.submitCertEvidence(
            userId, skillId, certName, certUrl, issuer
        );

        return Map.of(
            "success", result.isSuccess(),
            "message", result.getMessage(),
            "evidenceId", result.getEvidenceId() != null ? result.getEvidenceId() : 0,
            "support", result.getSupport()
        );
    }
}
