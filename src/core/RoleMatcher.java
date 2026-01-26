package com.careermappro.core;

import api.GPTService;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleMatcher {

    public static String generateRoleMatches(
            AnalyticsService analytics,
            ProficiencyService prof) {

        Map<String, Double> readiness = analytics.getDomainAverages();
        Map<String, Double> profs = prof.getAllProficiencies();

        String readinessSummary = readiness.entrySet().stream()
                .map(e -> e.getKey() + "=" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));

        String profSummary = profs.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> e.getKey() + ":" + String.format("%.1f", e.getValue()))
                .collect(Collectors.joining(", "));
        double actualReadiness = analytics.getOverallReadiness();
        String readinessLabel = analytics.getReadinessLabel(actualReadiness);

        String prompt = """
                You are an AI career advisor.
                IMPORTANT: The user's REAL verified global readiness is %.2f / 10 (%s).
                Do NOT invent or assume any different score.

                Domain readiness breakdown: %s
                Top proficiencies: %s

                Suggest 3 job roles that best fit the user's current profile.
                For each role, give:
                - Use EXACT readiness value: %.2f / 10 (%s) — do NOT change this.
                - 1-sentence justification.

                Finally, list 1-2 roles the user could reach in 6 months with focused learning.
                Keep the response concise, factual, and structured.
                """.formatted(actualReadiness, readinessLabel, readinessSummary, profSummary, actualReadiness,
                readinessLabel);

        GPTService gpt = new GPTService();
        String response = gpt.generateText(prompt);
        if (response == null || response.isBlank())
            return "GPT could not generate role matches right now.";
        return "\n💼 Career Role Match:\n" + response.trim();
    }
}
