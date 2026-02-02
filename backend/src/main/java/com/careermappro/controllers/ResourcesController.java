package com.careermappro.controllers;

import com.careermappro.services.GPTService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
@RequestMapping("/api/resources")
public class ResourcesController {

    private final GPTService gptService;

    public ResourcesController(GPTService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generateResources(@RequestBody Map<String, String> request) {
        String skill = request.get("skill");
        String level = request.get("level");
        String type = request.get("type");

        String prompt = String.format(
            "Generate 3 specific, real learning resources for %s at %s level. " +
            "Format: For each resource provide: Title, Brief Description (1 sentence), Platform, Duration, Free/Paid. " +
            "Focus on %s type resources. Be specific with real course names and platforms.",
            skill != null ? skill : "software development",
            level != null ? level : "intermediate",
            type != null ? type : "course"
        );

        String aiResponse = gptService.generateText(prompt);

        return Map.of("resources", aiResponse);
    }
}
