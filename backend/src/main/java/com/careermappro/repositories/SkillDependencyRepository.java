package com.careermappro.repositories;

import com.careermappro.entities.Skill;
import com.careermappro.entities.SkillDependency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillDependencyRepository extends JpaRepository<SkillDependency, Integer> {
    List<SkillDependency> findBySkill(Skill skill);
}
