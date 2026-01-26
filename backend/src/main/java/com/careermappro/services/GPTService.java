package com.careermappro.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GPTService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateText(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "OpenAI API key not configured. Please set openai.api.key in application.properties";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", 2000);
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                OPENAI_API_URL,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");
                    return (String) messageObj.get("content");
                }
            }

            return "No response from GPT";

        } catch (Exception e) {
            System.err.println("GPT API Error: " + e.getMessage());
            return "Error calling GPT API: " + e.getMessage();
        }
    }

    /**
     * Generate completion with system prompt and custom max tokens.
     * Used for structured curriculum generation.
     */
    public String generateCompletion(String systemPrompt, String userPrompt, int maxTokens) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "OpenAI API key not configured. Please set openai.api.key in application.properties";
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini"); // Use gpt-4o-mini for structured output
            requestBody.put("messages", List.of(systemMessage, userMessage));
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.3); // Lower temp for more consistent structure
            requestBody.put("response_format", Map.of("type", "json_object")); // Force JSON

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                OPENAI_API_URL,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) firstChoice.get("message");
                    return (String) messageObj.get("content");
                }
            }

            return "{}"; // Return empty JSON on failure

        } catch (Exception e) {
            System.err.println("GPT API Error: " + e.getMessage());
            throw new RuntimeException("Failed to generate curriculum: " + e.getMessage(), e);
        }
    }
}
