package com.careermappro.services;

import com.careermappro.entities.UserSkillState;
import com.careermappro.repositories.UserSkillStateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeAnalysisService {

    @Value("${openai.api.key}")
    private String openAIKey;

    private final UserSkillStateRepository userSkillStateRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeAnalysisService(UserSkillStateRepository userSkillStateRepository, OpenAIService openAIService) {
        this.userSkillStateRepository = userSkillStateRepository;
        this.openAIService = openAIService;
    }

    /**
     * Analyze resume from uploaded file
     */
    public Map<String, Object> analyzeResumeFile(Integer userId, MultipartFile file) {
        try {
            // Extract text from file
            String resumeText = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return analyzeResumeText(userId, resumeText);
        } catch (Exception e) {
            return Map.of("error", "Failed to parse resume file: " + e.getMessage());
        }
    }

    /**
     * Analyze resume from text input
     */
    public Map<String, Object> analyzeResumeText(Integer userId, String resumeText) {
        try {
            // Call OpenAI to extract skills
            Map<String, Object> analysis = callOpenAIForResumeAnalysis(resumeText);

            // Map extracted skills to node IDs across all paths
            Map<String, Object> matchedNodes = mapSkillsToNodes(analysis);

            return Map.of(
                    "userId", userId,
                    "skills", analysis.get("skills"),
                    "experience", analysis.get("experience"),
                    "matchedNodes", matchedNodes,
                    "timestamp", LocalDateTime.now().toString()
            );
        } catch (Exception e) {
            return Map.of("error", "Failed to analyze resume: " + e.getMessage());
        }
    }

    /**
     * Call OpenAI to analyze resume and extract skills
     */
    private Map<String, Object> callOpenAIForResumeAnalysis(String resumeText) throws Exception {
        String prompt = """
                Analyze this resume and extract:
                1. Technical skills (programming languages, frameworks, tools, databases, cloud platforms, etc.)
                2. Years of experience in each major area
                3. Proficiency level (beginner/intermediate/advanced) for each skill

                Return ONLY valid JSON in this exact format:
                {
                  "skills": [
                    { "name": "React", "experience": "2 years", "proficiency": "advanced" },
                    { "name": "Node.js", "experience": "3 years", "proficiency": "intermediate" }
                  ],
                  "experience": {
                    "frontend": 2,
                    "backend": 3,
                    "database": 2,
                    "security": 0,
                    "devops": 1
                  }
                }

                Resume:
                """ + resumeText;

        HttpClient client = HttpClient.newHttpClient();
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a technical resume analyzer. You must return ONLY valid JSON, no other text."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3,
                "response_format", Map.of("type", "json_object")
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAIKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API error: " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String content = root.path("choices").get(0).path("message").path("content").asText();

        // Parse JSON response
        return objectMapper.readValue(content, Map.class);
    }

    /**
     * Map extracted skills to node IDs across all role paths
     */
    private Map<String, Object> mapSkillsToNodes(Map<String, Object> analysis) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> skills = (List<Map<String, String>>) analysis.get("skills");

        Map<Integer, List<Integer>> matchedNodes = new HashMap<>();

        // Define skill-to-node mappings for each role
        // Role 1: Backend Engineer (nodes 1-30, competencies 101-123)
        Map<String, List<Integer>> backendSkillMap = Map.ofEntries(
                Map.entry("html", List.of(1, 2)),
                Map.entry("css", List.of(2, 3)),
                Map.entry("javascript", List.of(4, 5)),
                Map.entry("http", List.of(5, 101)),
                Map.entry("sql", List.of(6, 7, 8)),
                Map.entry("database", List.of(6, 7, 8, 9)),
                Map.entry("postgres", List.of(9, 105)),
                Map.entry("mysql", List.of(9, 105)),
                Map.entry("mongodb", List.of(106)),
                Map.entry("nosql", List.of(106)),
                Map.entry("node", List.of(10, 11)),
                Map.entry("express", List.of(11, 12)),
                Map.entry("rest", List.of(12, 13)),
                Map.entry("api", List.of(12, 13, 14)),
                Map.entry("graphql", List.of(102)),
                Map.entry("authentication", List.of(15, 16)),
                Map.entry("jwt", List.of(16, 111)),
                Map.entry("oauth", List.of(111)),
                Map.entry("security", List.of(17, 18)),
                Map.entry("testing", List.of(19, 20, 21)),
                Map.entry("docker", List.of(107)),
                Map.entry("kubernetes", List.of(108)),
                Map.entry("deployment", List.of(22, 23))
        );

        // Role 9: Frontend Developer (nodes 1-30, competencies 201-220)
        Map<String, List<Integer>> frontendSkillMap = Map.ofEntries(
                Map.entry("html", List.of(1, 2, 3)),
                Map.entry("css", List.of(4, 5, 6)),
                Map.entry("flexbox", List.of(6, 204)),
                Map.entry("grid", List.of(6, 204)),
                Map.entry("javascript", List.of(7, 8, 9, 10, 11, 12)),
                Map.entry("typescript", List.of(201)),
                Map.entry("react", List.of(13, 14, 15, 16, 17)),
                Map.entry("jsx", List.of(14)),
                Map.entry("hooks", List.of(16, 17)),
                Map.entry("state management", List.of(18, 19)),
                Map.entry("redux", List.of(19, 213)),
                Map.entry("context api", List.of(18)),
                Map.entry("routing", List.of(20)),
                Map.entry("api", List.of(21, 22)),
                Map.entry("webpack", List.of(203)),
                Map.entry("next", List.of(218)),
                Map.entry("testing", List.of(23, 24)),
                Map.entry("performance", List.of(25, 26))
        );

        // Role 10: ML Engineer
        Map<String, List<Integer>> mlSkillMap = Map.ofEntries(
                Map.entry("python", List.of(7)),
                Map.entry("sql", List.of(30)),
                Map.entry("database", List.of(30)),
                Map.entry("aws", List.of(11)),
                Map.entry("cloud", List.of(11)),
                Map.entry("tensorflow", List.of(46)),
                Map.entry("pytorch", List.of(47)),
                Map.entry("machine learning", List.of(48)),
                Map.entry("ml", List.of(48)),
                Map.entry("ai", List.of(48)),
                Map.entry("neural network", List.of(46, 47)),
                Map.entry("deep learning", List.of(46, 47)),
                Map.entry("statistics", List.of(49)),
                Map.entry("data science", List.of(48, 49)),
                Map.entry("numpy", List.of(7)),
                Map.entry("pandas", List.of(7)),
                Map.entry("scikit", List.of(48)),
                Map.entry("jupyter", List.of(7))
        );

        // Role 6: Mobile Developer
        Map<String, List<Integer>> mobileSkillMap = Map.ofEntries(
                Map.entry("git", List.of(14)),
                Map.entry("rest", List.of(32)),
                Map.entry("api", List.of(32)),
                Map.entry("react native", List.of(35)),
                Map.entry("swift", List.of(36)),
                Map.entry("ios", List.of(36)),
                Map.entry("kotlin", List.of(37)),
                Map.entry("android", List.of(37)),
                Map.entry("mobile", List.of(38)),
                Map.entry("ui", List.of(38)),
                Map.entry("firebase", List.of(39)),
                Map.entry("flutter", List.of(35))
        );

        // Role 2: DevOps Engineer
        Map<String, List<Integer>> devopsSkillMap = Map.ofEntries(
                Map.entry("python", List.of(7)),
                Map.entry("docker", List.of(9)),
                Map.entry("container", List.of(9)),
                Map.entry("kubernetes", List.of(10)),
                Map.entry("k8s", List.of(10)),
                Map.entry("aws", List.of(11)),
                Map.entry("cloud", List.of(11)),
                Map.entry("linux", List.of(20)),
                Map.entry("unix", List.of(20)),
                Map.entry("bash", List.of(20)),
                Map.entry("ci/cd", List.of(23)),
                Map.entry("jenkins", List.of(23)),
                Map.entry("github actions", List.of(23)),
                Map.entry("terraform", List.of(24)),
                Map.entry("infrastructure", List.of(24)),
                Map.entry("iac", List.of(24)),
                Map.entry("ansible", List.of(24)),
                Map.entry("monitoring", List.of(11))
        );

        // Match skills across all roles
        for (Map<String, String> skill : skills) {
            String skillName = skill.get("name").toLowerCase();
            String proficiency = skill.get("proficiency");

            // Only match intermediate/advanced skills
            if (proficiency.equals("intermediate") || proficiency.equals("advanced")) {
                // Backend Engineer (roleId: 5)
                for (Map.Entry<String, List<Integer>> entry : backendSkillMap.entrySet()) {
                    if (skillName.contains(entry.getKey())) {
                        matchedNodes.computeIfAbsent(5, k -> new ArrayList<>()).addAll(entry.getValue());
                    }
                }

                // Frontend Developer (roleId: 9)
                for (Map.Entry<String, List<Integer>> entry : frontendSkillMap.entrySet()) {
                    if (skillName.contains(entry.getKey())) {
                        matchedNodes.computeIfAbsent(9, k -> new ArrayList<>()).addAll(entry.getValue());
                    }
                }

                // ML Engineer (roleId: 10)
                for (Map.Entry<String, List<Integer>> entry : mlSkillMap.entrySet()) {
                    if (skillName.contains(entry.getKey())) {
                        matchedNodes.computeIfAbsent(10, k -> new ArrayList<>()).addAll(entry.getValue());
                    }
                }

                // Mobile Developer (roleId: 6)
                for (Map.Entry<String, List<Integer>> entry : mobileSkillMap.entrySet()) {
                    if (skillName.contains(entry.getKey())) {
                        matchedNodes.computeIfAbsent(6, k -> new ArrayList<>()).addAll(entry.getValue());
                    }
                }

                // DevOps Engineer (roleId: 2)
                for (Map.Entry<String, List<Integer>> entry : devopsSkillMap.entrySet()) {
                    if (skillName.contains(entry.getKey())) {
                        matchedNodes.computeIfAbsent(2, k -> new ArrayList<>()).addAll(entry.getValue());
                    }
                }
            }
        }

        // Remove duplicates
        for (Map.Entry<Integer, List<Integer>> entry : matchedNodes.entrySet()) {
            matchedNodes.put(entry.getKey(), entry.getValue().stream().distinct().sorted().toList());
        }

        return Map.of("roles", matchedNodes);
    }

    /**
     * Mark skills as completed in database
     */
    public Map<String, Object> markSkillsAsCompleted(Integer userId, Map<Integer, Object> matchedNodes) {
        try {
            int totalMarked = 0;

            for (Map.Entry<Integer, Object> entry : matchedNodes.entrySet()) {
                Integer roleId = entry.getKey();
                @SuppressWarnings("unchecked")
                List<Integer> nodeIds = (List<Integer>) entry.getValue();

                for (Integer nodeId : nodeIds) {
                    // Check if already exists
                    Optional<UserSkillState> existing = userSkillStateRepository.findByUserIdAndSkillId(userId, nodeId);

                    if (existing.isEmpty()) {
                        UserSkillState state = new UserSkillState();
                        state.setUserId(userId);
                        state.setSkillId(nodeId);
                        state.setStatus(UserSkillState.SkillStatus.PROVED); // Resume proves competence
                        state.setConfidence(0.8); // High confidence from resume
                        state.setEvidenceScore(0.8);
                        state.setLastEvidenceAt(LocalDateTime.now());
                        state.setUpdatedAt(LocalDateTime.now());
                        userSkillStateRepository.save(state);
                        totalMarked++;
                    }
                }
            }

            return Map.of(
                    "success", true,
                    "markedCount", totalMarked,
                    "message", "Successfully marked " + totalMarked + " skills as completed"
            );
        } catch (Exception e) {
            return Map.of("error", "Failed to mark skills: " + e.getMessage());
        }
    }
}
