package com.careermappro.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
public class HealthController {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());

        // Check database connectivity
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                response.put("database", "connected");
            } else {
                response.put("database", "not_configured");
            }
        } catch (Exception e) {
            response.put("database", "error: " + e.getMessage());
        }

        // Check OpenAI key presence (not validity, just presence)
        String openAiKey = System.getenv("OPENAI_API_KEY");
        response.put("openai_configured", openAiKey != null && !openAiKey.isEmpty());

        return response;
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        Map<String, String> response = new HashMap<>();
        response.put("version", "1.0.0-production");
        response.put("build", "2026-01-11");
        response.put("environment", System.getenv("ENV") != null ? System.getenv("ENV") : "development");
        return response;
    }
}
