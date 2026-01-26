package com.careermappro.repositories;

import com.careermappro.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    List<Quiz> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Quiz> findByUserIdAndSkillNameOrderByCreatedAtDesc(Integer userId, String skillName);
    List<Quiz> findByUserIdAndStatus(Integer userId, Quiz.QuizStatus status);
}
