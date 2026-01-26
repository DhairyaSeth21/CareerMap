package com.careermappro.repositories;

import com.careermappro.entities.Proficiency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProficiencyRepository extends JpaRepository<Proficiency, Integer> {
    List<Proficiency> findByUserId(Integer userId);
    Optional<Proficiency> findByUserIdAndSkill(Integer userId, String skill);
}
