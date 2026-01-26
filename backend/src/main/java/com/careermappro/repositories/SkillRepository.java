package com.careermappro.repositories;

import com.careermappro.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    Optional<Skill> findByName(String name);

    List<Skill> findByCategory(String category);

    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Skill> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category")
    List<String> findAllCategories();
}
