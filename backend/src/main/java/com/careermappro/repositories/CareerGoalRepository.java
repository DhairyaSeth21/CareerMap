package com.careermappro.repositories;

import com.careermappro.entities.CareerGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerGoalRepository extends JpaRepository<CareerGoal, Integer> {
    List<CareerGoal> findByUserId(Integer userId);
}
