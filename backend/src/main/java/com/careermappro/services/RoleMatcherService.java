package com.careermappro.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleMatcherService {

    private final AnalyticsService analyticsService;
    private final ProficiencyService proficiencyService;
    private final GPTService gptService;

    public RoleMatcherService(
            AnalyticsService analyticsService,
            ProficiencyService proficiencyService,
            GPTService gptService) {
        this.analyticsService = analyticsService;
        this.proficiencyService = proficiencyService;
        this.gptService = gptService;
    }

    public String generateRoleMatches(Integer userId) {
        Map<String, Double> domainReadiness = analyticsService.getDomainAverages(userId);
        Map<String, Double> proficiencies = proficiencyService.getAllProficiencies(userId);

        String readinessSummary = domainReadiness.entrySet().stream()
                .map(e -> e.getKey() + "=" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));

        String profSummary = proficiencies.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> e.getKey() + ":" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));

        double overallReadiness = analyticsService.getOverallReadiness(userId);
        String readinessLabel = analyticsService.getReadinessLabel(overallReadiness);

        String prompt = String.format("""
                You are an AI career advisor.
                IMPORTANT: The user's REAL verified global readiness is %.2f / 10 (%s).
                Do NOT invent or assume any different score.

                Domain readiness breakdown: %s
                Top proficiencies: %s

                Suggest 3 job roles that best fit the user's current profile.
                For each role, give:
                - Role name
                - Use EXACT readiness value: %.2f / 10 (%s) — do NOT change this.
                - 1-sentence justification based on their skills.

                Finally, list 1-2 roles the user could reach in 6 months with focused learning.
                Keep the response concise, factual, and structured.
                """,
                overallReadiness, readinessLabel,
                readinessSummary, profSummary,
                overallReadiness, readinessLabel);

        String response = gptService.generateText(prompt);
        if (response == null || response.isBlank()) {
            return "GPT could not generate role matches right now.";
        }
        return response.trim();
    }

    public String analyzeJobDescription(Integer userId, String jobDescription) {
        Map<String, Double> proficiencies = proficiencyService.getAllProficiencies(userId);
        double overallReadiness = analyticsService.getOverallReadiness(userId);

        String profSummary = proficiencies.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(e -> e.getKey() + " (Level " + String.format("%.1f", e.getValue()) + "/10)")
                .collect(Collectors.joining(", "));

        String prompt = String.format("""
                You are an AI career advisor analyzing a job posting.

                User's Current Skills: %s
                Overall Readiness: %.2f / 10

                Job Description:
                %s

                Please analyze:
                1. Calculate readiness score (0-10) for THIS specific job based on their skills.
                2. List skills they HAVE that match the job requirements.
                3. List skills they're MISSING that are required.
                4. Give 3 specific learning recommendations to close the gaps.

                Be concise and actionable.
                """,
                profSummary, overallReadiness, jobDescription);

        String response = gptService.generateText(prompt);
        if (response == null || response.isBlank()) {
            return "GPT could not analyze the job description right now.";
        }
        return response.trim();
    }
}
