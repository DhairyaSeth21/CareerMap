package com.careermappro.repositories;

import com.careermappro.entities.DeepPathStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeepPathStepRepository extends JpaRepository<DeepPathStep, Integer> {
    List<DeepPathStep> findByDeepPath_DeepPathIdOrderByWeekNumberAscOrderInWeekAsc(Integer deepPathId);
    List<DeepPathStep> findByDeepPath_DeepPathIdAndWeekNumber(Integer deepPathId, Integer weekNumber);
}
