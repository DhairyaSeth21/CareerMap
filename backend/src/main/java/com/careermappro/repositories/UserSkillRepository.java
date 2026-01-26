package com.careermappro.repositories;

import com.careermappro.entities.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Integer> {

    List<UserSkill> findByUserId(Integer userId);

    @Query("SELECT us FROM UserSkill us WHERE us.userId = :userId AND us.skill.skillId = :skillId")
    Optional<UserSkill> findByUserIdAndSkillId(@Param("userId") Integer userId, @Param("skillId") Integer skillId);

    @Query("SELECT us FROM UserSkill us WHERE us.userId = :userId AND us.skill.category = :category")
    List<UserSkill> findByUserIdAndCategory(@Param("userId") Integer userId, @Param("category") String category);

    @Query("SELECT AVG(us.proficiencyLevel) FROM UserSkill us WHERE us.userId = :userId")
    Double getAverageProficiency(@Param("userId") Integer userId);
}
