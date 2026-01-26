package com.careermappro.repositories;

import com.careermappro.entities.PathStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathStepRepository extends JpaRepository<PathStep, Integer> {

    /**
     * Find all steps in a specific unit
     */
    List<PathStep> findByUnitId(Integer unitId);

    /**
     * Find all steps in a unit ordered by step number
     */
    List<PathStep> findByUnitIdOrderByStepNumber(Integer unitId);

    /**
     * Find steps by status
     */
    List<PathStep> findByStatus(PathStep.StepStatus status);

    /**
     * Find steps in a unit with a specific status
     */
    List<PathStep> findByUnitIdAndStatus(Integer unitId, PathStep.StepStatus status);

    /**
     * Find a specific step by unit and step number
     */
    Optional<PathStep> findByUnitIdAndStepNumber(Integer unitId, Integer stepNumber);

    /**
     * Find steps related to a specific skill
     */
    List<PathStep> findBySkillId(Integer skillId);

    /**
     * Find steps in a unit for a specific skill
     */
    Optional<PathStep> findByUnitIdAndSkillId(Integer unitId, Integer skillId);

    /**
     * Find locked steps in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.status = 'LOCKED' ORDER BY ps.stepNumber")
    List<PathStep> findLockedSteps(@Param("unitId") Integer unitId);

    /**
     * Find available steps in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.status = 'AVAILABLE' ORDER BY ps.stepNumber")
    List<PathStep> findAvailableSteps(@Param("unitId") Integer unitId);

    /**
     * Find in-progress steps in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.status = 'IN_PROGRESS' ORDER BY ps.stepNumber")
    List<PathStep> findInProgressSteps(@Param("unitId") Integer unitId);

    /**
     * Find completed steps in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.status = 'COMPLETED' ORDER BY ps.stepNumber")
    List<PathStep> findCompletedSteps(@Param("unitId") Integer unitId);

    /**
     * Count total steps in a unit
     */
    long countByUnitId(Integer unitId);

    /**
     * Count completed steps in a unit
     */
    long countByUnitIdAndStatus(Integer unitId, PathStep.StepStatus status);

    /**
     * Find the first available step in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.status = 'AVAILABLE' ORDER BY ps.stepNumber LIMIT 1")
    Optional<PathStep> findFirstAvailableStep(@Param("unitId") Integer unitId);

    /**
     * Find the next step after a specific step number
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.stepNumber > :stepNumber ORDER BY ps.stepNumber LIMIT 1")
    Optional<PathStep> findNextStep(@Param("unitId") Integer unitId, @Param("stepNumber") Integer stepNumber);

    /**
     * Find steps that have been started in a unit
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.startedAt IS NOT NULL ORDER BY ps.stepNumber")
    List<PathStep> findStartedSteps(@Param("unitId") Integer unitId);

    /**
     * Find steps with insufficient evidence submitted
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.evidenceCount = 0 ORDER BY ps.stepNumber")
    List<PathStep> findStepsWithoutEvidence(@Param("unitId") Integer unitId);

    /**
     * Find steps with specific skill and status
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.skillId = :skillId AND ps.status = :status ORDER BY ps.stepNumber")
    List<PathStep> findBySkillIdAndStatus(@Param("skillId") Integer skillId, @Param("status") PathStep.StepStatus status);

    /**
     * Find steps ordered by last activity
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId ORDER BY COALESCE(ps.startedAt, ps.unlockedAt) DESC NULLS LAST")
    List<PathStep> findByUnitIdOrderByLastActivity(@Param("unitId") Integer unitId);

    /**
     * Find steps with evidence count greater than zero
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.evidenceCount > 0 ORDER BY ps.stepNumber")
    List<PathStep> findStepsWithEvidence(@Param("unitId") Integer unitId);

    /**
     * Find steps with high attempt count (may need help)
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId AND ps.attempts >= :minAttempts ORDER BY ps.attempts DESC")
    List<PathStep> findStepsWithHighAttempts(@Param("unitId") Integer unitId, @Param("minAttempts") Integer minAttempts);

    /**
     * Find steps ordered by progress (by evidence count)
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.unitId = :unitId ORDER BY ps.evidenceCount DESC, ps.stepNumber")
    List<PathStep> findByUnitIdOrderByProgress(@Param("unitId") Integer unitId);

    /**
     * Check if a step exists in a unit
     */
    boolean existsByUnitIdAndStepId(Integer unitId, Integer stepId);
}
