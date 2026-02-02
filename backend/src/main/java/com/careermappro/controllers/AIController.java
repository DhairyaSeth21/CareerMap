package com.careermappro.controllers;

import com.careermappro.services.RoleMatcherService;
import com.careermappro.services.GPTService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
@RequestMapping("/api/ai")
public class AIController {

    private final RoleMatcherService roleMatcherService;
    private final GPTService gptService;

    public AIController(RoleMatcherService roleMatcherService, GPTService gptService) {
        this.roleMatcherService = roleMatcherService;
        this.gptService = gptService;
    }

    @GetMapping("/role-matches/{userId}")
    public Map<String, String> getRoleMatches(@PathVariable Integer userId) {
        String result = roleMatcherService.generateRoleMatches(userId);
        return Map.of("result", result);
    }

    @PostMapping("/analyze-job")
    public Map<String, String> analyzeJob(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String jobDescription = (String) request.get("jobDescription");

        String result = roleMatcherService.analyzeJobDescription(userId, jobDescription);
        return Map.of("result", result);
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = gptService.generateText(prompt);
        return Map.of("response", response);
    }
}
