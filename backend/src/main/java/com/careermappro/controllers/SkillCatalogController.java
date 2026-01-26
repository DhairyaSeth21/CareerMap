package com.careermappro.controllers;

import com.careermappro.entities.Skill;
import com.careermappro.repositories.SkillRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
public class SkillCatalogController {

    private final SkillRepository skillRepository;

    public SkillCatalogController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * GET /api/skills/catalog
     * Get all available skills that users can assess
     */
    @GetMapping("/catalog")
    public List<Map<String, Object>> getSkillCatalog() {
        List<Skill> skills = skillRepository.findAll();

        return skills.stream().map(skill -> {
            Map<String, Object> item = new HashMap<>();
            item.put("skillId", skill.getSkillId());
            item.put("name", skill.getName());
            item.put("category", skill.getCategory());
            item.put("description", skill.getDescription());
            item.put("difficultyLevel", skill.getDifficultyLevel() != null ? skill.getDifficultyLevel().toString() : "Intermediate");
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * GET /api/skills/catalog/categories
     * Get unique categories
     */
    @GetMapping("/catalog/categories")
    public List<String> getCategories() {
        return skillRepository.findAll().stream()
            .map(Skill::getCategory)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
