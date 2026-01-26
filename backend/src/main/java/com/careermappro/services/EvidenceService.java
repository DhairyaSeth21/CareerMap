package com.careermappro.services;

import com.careermappro.entities.Evidence;
import com.careermappro.entities.EvidenceSkillLink;
import com.careermappro.repositories.EvidenceRepository;
import com.careermappro.repositories.EvidenceSkillLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EvidenceService enforces STRICT evidence requirements.
 *
 * Core principle: "Show me the code" - We don't take your word for it.
 *
 * Evidence scoring rules (STRICT):
 * - QUIZ: Auto-scored via AssessmentResultService (already strict)
 * - CERT: High-trust (0.85) if verifiable URL provided
 * - PROJECT: MAX 0.30 without code/artifacts
 * - PROJECT with code: 0.40-0.70 based on OpenAI review
 * - REPO: 0.50-0.80 based on commit history + OpenAI review
 * - WORK_SAMPLE: 0.60-0.85 based on production evidence
 */
@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceSkillLinkRepository evidenceSkillLinkRepository;
    private final StateTransitionService stateTransitionService;
    private final GPTService gptService;

    public EvidenceService(
            EvidenceRepository evidenceRepository,
            EvidenceSkillLinkRepository evidenceSkillLinkRepository,
            StateTransitionService stateTransitionService,
            GPTService gptService) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceSkillLinkRepository = evidenceSkillLinkRepository;
        this.stateTransitionService = stateTransitionService;
        this.gptService = gptService;
    }

    /**
     * Submit PROJECT evidence with STRICT validation.
     *
     * Requirements:
     * - Must provide description
     * - Must provide GitHub URL OR code sample
     * - Without artifacts: MAX support = 0.30 (weak evidence)
     * - With artifacts: OpenAI review determines 0.40-0.70
     */
    @Transactional
    public EvidenceSubmissionResult submitProjectEvidence(
            Integer userId,
            Integer skillId,
            String description,
            String githubUrl,
            String codeSample) {

        System.out.println("=== STRICT PROJECT EVIDENCE SUBMISSION ===");
        System.out.println("userId=" + userId + ", skillId=" + skillId);
        System.out.println("description: " + (description != null ? description.substring(0, Math.min(50, description.length())) : "NONE"));
        System.out.println("githubUrl: " + (githubUrl != null ? githubUrl : "NONE"));
        System.out.println("codeSample: " + (codeSample != null ? "PROVIDED (" + codeSample.length() + " chars)" : "NONE"));

        // Validation: Description required
        if (description == null || description.trim().isEmpty()) {
            return new EvidenceSubmissionResult(false, "Description is required", null, 0.0);
        }

        // Check if artifacts provided
        boolean hasArtifacts = (githubUrl != null && !githubUrl.trim().isEmpty()) ||
                               (codeSample != null && !codeSample.trim().isEmpty());

        double support;
        String reviewSummary;

        if (!hasArtifacts) {
            // NO ARTIFACTS: Weak evidence, max support 0.30
            support = 0.30;
            reviewSummary = "No code or repository provided. Weak evidence - claims cannot be verified.";
            System.out.println("⚠️  NO ARTIFACTS - Support capped at 0.30");
        } else {
            // HAS ARTIFACTS: OpenAI review
            System.out.println("✓ Artifacts provided - Running OpenAI review...");

            String codeToReview = codeSample != null ? codeSample :
                                 "GitHub Repository: " + githubUrl + "\n\nNote: Review based on repo URL only.";

            String reviewResult = reviewProjectWithOpenAI(skillId, description, codeToReview);

            // Parse review (simplified - in production, use structured JSON)
            support = parseReviewSupport(reviewResult);
            reviewSummary = reviewResult;

            System.out.println("OpenAI Review Support: " + String.format("%.2f", support));
        }

        // Create evidence record
        String rawText = buildProjectEvidenceText(description, githubUrl, codeSample, reviewSummary);
        Evidence evidence = new Evidence(userId, Evidence.EvidenceType.PROJECT, rawText);
        evidence.setSourceUri(githubUrl);
        evidence = evidenceRepository.save(evidence);

        // Create skill link
        EvidenceSkillLink link = new EvidenceSkillLink(
            evidence.getEvidenceId(),
            skillId,
            support,
            hasArtifacts ? "openai-review" : "self-reported"
        );
        link.setConfidence(hasArtifacts ? 0.85 : 0.30); // Confidence in the link itself
        evidenceSkillLinkRepository.save(link);

        // Trigger state machine
        stateTransitionService.updateStateFromEvidence(userId, skillId, support, "PROJECT");
        stateTransitionService.recomputeFrontierForSkill(userId, skillId);

        System.out.println("=== PROJECT EVIDENCE ACCEPTED ===");
        System.out.println("evidenceId=" + evidence.getEvidenceId() + ", support=" + String.format("%.2f", support));

        return new EvidenceSubmissionResult(
            true,
            "Evidence submitted successfully",
            evidence.getEvidenceId(),
            support
        );
    }

    /**
     * Submit CERT evidence (high-trust if URL provided).
     */
    @Transactional
    public EvidenceSubmissionResult submitCertEvidence(
            Integer userId,
            Integer skillId,
            String certName,
            String certUrl,
            String issuer) {

        if (certName == null || certName.trim().isEmpty()) {
            return new EvidenceSubmissionResult(false, "Certificate name is required", null, 0.0);
        }

        // High support for verifiable certs
        double support = (certUrl != null && !certUrl.trim().isEmpty()) ? 0.85 : 0.60;

        String rawText = String.format("Certificate: %s | Issuer: %s | URL: %s",
                                       certName, issuer, certUrl);

        Evidence evidence = new Evidence(userId, Evidence.EvidenceType.CERT, rawText);
        evidence.setSourceUri(certUrl);
        evidence = evidenceRepository.save(evidence);

        EvidenceSkillLink link = new EvidenceSkillLink(
            evidence.getEvidenceId(),
            skillId,
            support,
            "cert-submission"
        );
        link.setConfidence(1.0); // High confidence in cert links
        evidenceSkillLinkRepository.save(link);

        stateTransitionService.updateStateFromEvidence(userId, skillId, support, "CERT");
        stateTransitionService.recomputeFrontierForSkill(userId, skillId);

        return new EvidenceSubmissionResult(true, "Certificate evidence submitted", evidence.getEvidenceId(), support);
    }

    /**
     * Review project code using OpenAI.
     * Evaluates against FAANG-level standards.
     */
    private String reviewProjectWithOpenAI(Integer skillId, String description, String code) {
        String prompt = String.format("""
                You are a senior software engineer evaluating a project submission for skill proficiency.

                Project Description: %s

                Code Sample:
                ```
                %s
                ```

                Evaluate this code against professional standards:
                1. Does the code demonstrate actual competency in the skill?
                2. Is the code well-structured and production-ready?
                3. Are there obvious issues, security flaws, or anti-patterns?
                4. Does this match the claimed skill level?

                Provide a support score from 0.40 to 0.70:
                - 0.40-0.50: Basic implementation, significant issues
                - 0.50-0.60: Functional but not production-grade
                - 0.60-0.65: Good quality, minor improvements needed
                - 0.65-0.70: Excellent, production-ready code

                Respond with:
                SUPPORT: [score]
                REVIEW: [2-3 sentence summary]
                """, description, code.substring(0, Math.min(2000, code.length())));

        try {
            return gptService.generateText(prompt);
        } catch (Exception e) {
            System.err.println("OpenAI review failed: " + e.getMessage());
            // Fallback: Give benefit of doubt but not full score
            return "SUPPORT: 0.50\nREVIEW: Unable to review code automatically. Moderate support awarded.";
        }
    }

    /**
     * Parse support score from OpenAI review response.
     */
    private double parseReviewSupport(String reviewResult) {
        try {
            // Extract "SUPPORT: X.XX" from response
            String[] lines = reviewResult.split("\n");
            for (String line : lines) {
                if (line.startsWith("SUPPORT:")) {
                    String scoreStr = line.substring(8).trim();
                    double score = Double.parseDouble(scoreStr);
                    // Clamp to valid range
                    return Math.max(0.40, Math.min(0.70, score));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse review support: " + e.getMessage());
        }
        // Fallback: Middle of range
        return 0.50;
    }

    /**
     * Build evidence raw text for PROJECT.
     */
    private String buildProjectEvidenceText(String description, String githubUrl, String codeSample, String review) {
        StringBuilder sb = new StringBuilder();
        sb.append("PROJECT EVIDENCE\n");
        sb.append("=================\n\n");
        sb.append("Description:\n").append(description).append("\n\n");

        if (githubUrl != null) {
            sb.append("Repository: ").append(githubUrl).append("\n\n");
        }

        if (codeSample != null) {
            sb.append("Code Sample:\n").append(codeSample).append("\n\n");
        }

        sb.append("Review:\n").append(review).append("\n");

        return sb.toString();
    }

    /**
     * Result DTO for evidence submission.
     */
    public static class EvidenceSubmissionResult {
        private final boolean success;
        private final String message;
        private final Integer evidenceId;
        private final double support;

        public EvidenceSubmissionResult(boolean success, String message, Integer evidenceId, double support) {
            this.success = success;
            this.message = message;
            this.evidenceId = evidenceId;
            this.support = support;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Integer getEvidenceId() { return evidenceId; }
        public double getSupport() { return support; }
    }
}
