package com.careermappro.services;

import com.careermappro.entities.Proficiency;
import com.careermappro.repositories.ProficiencyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProficiencyService {

    private final ProficiencyRepository repo;

    public ProficiencyService(ProficiencyRepository repo) {
        this.repo = repo;
    }

    public List<Proficiency> getUserSkills(Integer userId) {
        return repo.findByUserId(userId);
    }

    public Proficiency save(Proficiency proficiency) {
        return repo.save(proficiency);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    /**
     * Get all proficiencies as a Map for a user
     * Used by RoleMatcher for GPT analysis
     */
    public Map<String, Double> getAllProficiencies(Integer userId) {
        List<Proficiency> skills = repo.findByUserId(userId);
        Map<String, Double> profMap = new HashMap<>();

        for (Proficiency p : skills) {
            profMap.put(p.getSkill(), p.getProficiency());
        }

        return profMap;
    }
}
