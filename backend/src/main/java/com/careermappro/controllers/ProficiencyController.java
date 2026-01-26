package com.careermappro.controllers;

import com.careermappro.entities.Proficiency;
import com.careermappro.services.ProficiencyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
@RequestMapping("/api/proficiencies")
public class ProficiencyController {

    private final ProficiencyService service;

    public ProficiencyController(ProficiencyService service) {
        this.service = service;
    }

    /**
     * GET /api/proficiencies/{userId}
     * Get all proficiencies for a user (READ-ONLY)
     */
    @GetMapping("/{userId}")
    public List<Proficiency> getUserSkills(@PathVariable Integer userId) {
        return service.getUserSkills(userId);
    }

    /**
     * POST /api/proficiencies
     * DISABLED - Manual skill editing not allowed
     * Skills can only be updated through quiz completion
     */
    @PostMapping
    public Proficiency saveSkill(@RequestBody Proficiency p) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Manual skill editing is disabled. Complete quizzes to update proficiency."
        );
    }

    /**
     * DELETE /api/proficiencies/{id}
     * DISABLED - Manual skill deletion not allowed
     */
    @DeleteMapping("/{id}")
    public void deleteSkill(@PathVariable Integer id) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Manual skill deletion is disabled. Proficiencies are managed through quizzes."
        );
    }
}
