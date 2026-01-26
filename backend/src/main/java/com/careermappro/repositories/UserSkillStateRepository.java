package com.careermappro.repositories;

import com.careermappro.entities.UserSkillState;
import com.careermappro.entities.UserSkillState.SkillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillStateRepository extends JpaRepository<UserSkillState, Integer> {

    Optional<UserSkillState> findByUserIdAndSkillId(Integer userId, Integer skillId);

    List<UserSkillState> findByUserId(Integer userId);

    List<UserSkillState> findByUserIdAndStatus(Integer userId, SkillStatus status);

    @Query("SELECT u FROM UserSkillState u WHERE u.userId = :userId AND u.status IN :statuses")
    List<UserSkillState> findByUserIdAndStatusIn(Integer userId, List<SkillStatus> statuses);

    @Query("SELECT u FROM UserSkillState u WHERE u.staleAt IS NOT NULL AND u.staleAt < :now")
    List<UserSkillState> findStaleStates(LocalDateTime now);

    @Query("SELECT u FROM UserSkillState u WHERE u.userId = :userId AND u.skillId IN :skillIds")
    List<UserSkillState> findByUserIdAndSkillIdIn(Integer userId, List<Integer> skillIds);
}
