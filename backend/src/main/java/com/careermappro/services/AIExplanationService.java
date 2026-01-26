package com.careermappro.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * AIExplanationService
 * Generates comprehensive, retention-focused explanations using OpenAI
 *
 * Key Features:
 * - Uses learning resources to provide context
 * - Focuses on 100% retention through analogies and examples
 * - Provides real-world use cases
 * - Explains WHY not just WHAT
 */
@Service
public class AIExplanationService {

    @Value("${openai.api.key:#{null}}")
    private String openaiApiKey;

    private final ObjectMapper objectMapper;

    public AIExplanationService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate AI explanation for a skill node using provided data
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> generateExplanation(Map<String, Object> skillData) {
        // Validate API key
        if (openaiApiKey == null || openaiApiKey.isEmpty() || openaiApiKey.equals("your-api-key-here")) {
            throw new IllegalStateException("OpenAI API key not configured. Set OPENAI_API_KEY environment variable.");
        }

        // Extract skill information from request
        String skillName = (String) skillData.get("skillName");
        String whyItMatters = (String) skillData.get("whyItMatters");
        String proofRequirement = (String) skillData.get("proofRequirement");
        List<Map<String, Object>> learnResources = (List<Map<String, Object>>) skillData.get("learnResources");

        // Build comprehensive prompt
        String prompt = buildExplanationPrompt(skillName, whyItMatters, proofRequirement, learnResources);

        // Call OpenAI API
        String explanation = callOpenAI(prompt);

        // Return structured response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("skillName", skillName);
        response.put("explanation", explanation);
        return response;
    }

    /**
     * Build a comprehensive prompt for maximum retention and practical understanding
     */
    private String buildExplanationPrompt(String skillName, String whyItMatters, String proofRequirement, List<Map<String, Object>> learnResources) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert teacher focused on 100% retention and practical application.\n\n");
        prompt.append("Explain the following concept to a software engineer:\n\n");
        prompt.append("TOPIC: ").append(skillName).append("\n\n");
        prompt.append("CONTEXT: ").append(whyItMatters).append("\n\n");

        if (proofRequirement != null && !proofRequirement.isEmpty()) {
            prompt.append("PROOF REQUIREMENT: ").append(proofRequirement).append("\n\n");
        }

        // Include learning resources if available
        if (learnResources != null && !learnResources.isEmpty()) {
            prompt.append("AVAILABLE LEARNING RESOURCES:\n");
            for (Map<String, Object> resource : learnResources) {
                prompt.append("- ").append(resource.get("title"))
                      .append(" (").append(resource.get("type")).append(")\n");
            }
            prompt.append("\n");
        }

        prompt.append("Your explanation MUST include:\n\n");

        prompt.append("## 1. THE CORE CONCEPT\n");
        prompt.append("In 2-3 sentences, explain:\n");
        prompt.append("- What is it in simple terms?\n");
        prompt.append("- Why does it exist? What problem does it solve?\n\n");

        prompt.append("## 2. THE PERFECT ANALOGY\n");
        prompt.append("Create a memorable real-world analogy that makes this concept instantly click. ");
        prompt.append("Make it visual and relatable to everyday life.\n\n");

        prompt.append("## 3. REAL-WORLD USE CASE\n");
        prompt.append("Describe a concrete production scenario with specifics:\n");
        prompt.append("- What company/product uses this?\n");
        prompt.append("- What exact problem does it solve for them?\n");
        prompt.append("- What would break if they didn't use it?\n\n");

        prompt.append("## 4. KEY TECHNICAL DETAILS\n");
        prompt.append("The 3-5 most important technical aspects (bullet points):\n");
        prompt.append("- Focus on what you MUST know, not nice-to-haves\n");
        prompt.append("- Include specific syntax, patterns, or APIs if relevant\n\n");

        prompt.append("## 5. COMMON PITFALLS\n");
        prompt.append("What mistakes do engineers make with this?\n");
        prompt.append("- List 2-3 specific errors or misconceptions\n");
        prompt.append("- Explain the RIGHT way to think about it\n\n");

        prompt.append("## 6. MASTERY CHECKLIST\n");
        prompt.append("To prove you truly understand this, you should be able to:\n");
        prompt.append("- [ ] (List 3-5 specific tasks or questions)\n\n");

        prompt.append("FORMATTING RULES:\n");
        prompt.append("- Use markdown headers (##)\n");
        prompt.append("- Keep each section concise (3-5 sentences max)\n");
        prompt.append("- Use bullet points for lists\n");
        prompt.append("- Be specific with examples (company names, code snippets, etc.)\n");
        prompt.append("- Write like you're teaching a friend over coffee, not writing a textbook\n");

        return prompt.toString();
    }

    /**
     * Call OpenAI API using standard Java HttpURLConnection
     */
    private String callOpenAI(String prompt) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", new Object[]{
                Map.of(
                    "role", "system",
                    "content", "You are an expert technical educator who makes complex concepts stick. You use memorable analogies, real-world examples, and focus on what engineers actually need to know. Your explanations are concise, practical, and designed for 100% retention."
                ),
                Map.of(
                    "role", "user",
                    "content", prompt
                )
            });
            requestBody.put("max_tokens", 2500);
            requestBody.put("temperature", 0.7);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Create HTTP connection
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + openaiApiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                try (Scanner scanner = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8)) {
                    String errorBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    throw new IOException("OpenAI API call failed: " + responseCode + " - " + errorBody);
                }
            }

            String responseBody;
            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
                responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }

            // Parse response
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            return jsonResponse.get("choices").get(0).get("message").get("content").asText();

        } catch (IOException e) {
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }
}
