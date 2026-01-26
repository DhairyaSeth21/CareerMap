package com.careermappro.repositories;

import com.careermappro.entities.PathUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathUnitRepository extends JpaRepository<PathUnit, Integer> {

    /**
     * Find all units for a specific learning path
     */
    List<PathUnit> findByPathId(Integer pathId);

    /**
     * Find all units for a learning path ordered by unit number
     */
    List<PathUnit> findByPathIdOrderByUnitNumber(Integer pathId);

    /**
     * Find units by their status
     */
    List<PathUnit> findByStatus(PathUnit.UnitStatus status);

    /**
     * Find units for a path with a specific status
     */
    List<PathUnit> findByPathIdAndStatus(Integer pathId, PathUnit.UnitStatus status);

    /**
     * Find a specific unit in a path by unit number
     */
    Optional<PathUnit> findByPathIdAndUnitNumber(Integer pathId, Integer unitNumber);

    /**
     * Find locked units in a path
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.status = 'LOCKED' ORDER BY pu.unitNumber")
    List<PathUnit> findLockedUnits(@Param("pathId") Integer pathId);

    /**
     * Find available units in a path
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.status = 'AVAILABLE' ORDER BY pu.unitNumber")
    List<PathUnit> findAvailableUnits(@Param("pathId") Integer pathId);

    /**
     * Find in-progress units in a path
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.status = 'IN_PROGRESS' ORDER BY pu.unitNumber")
    List<PathUnit> findInProgressUnits(@Param("pathId") Integer pathId);

    /**
     * Find completed units in a path
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.status = 'COMPLETED' ORDER BY pu.unitNumber")
    List<PathUnit> findCompletedUnits(@Param("pathId") Integer pathId);

    /**
     * Count total units in a path
     */
    long countByPathId(Integer pathId);

    /**
     * Count completed units in a path
     */
    long countByPathIdAndStatus(Integer pathId, PathUnit.UnitStatus status);

    /**
     * Find the first available unit in a path (for progression)
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.status = 'AVAILABLE' ORDER BY pu.unitNumber LIMIT 1")
    Optional<PathUnit> findFirstAvailableUnit(@Param("pathId") Integer pathId);

    /**
     * Find the next unit after a specific unit number
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.unitNumber > :unitNumber ORDER BY pu.unitNumber LIMIT 1")
    Optional<PathUnit> findNextUnit(@Param("pathId") Integer pathId, @Param("unitNumber") Integer unitNumber);

    /**
     * Find units that have been started (started_at is not null)
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.startedAt IS NOT NULL ORDER BY pu.unitNumber")
    List<PathUnit> findStartedUnits(@Param("pathId") Integer pathId);

    /**
     * Find units ordered by last activity (started_at or unlocked_at)
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId ORDER BY COALESCE(pu.startedAt, pu.unlockedAt) DESC NULLS LAST")
    List<PathUnit> findByPathIdOrderByLastActivity(@Param("pathId") Integer pathId);

    /**
     * Find units with estimated hours between a range
     */
    @Query("SELECT pu FROM PathUnit pu WHERE pu.pathId = :pathId AND pu.estimatedHours BETWEEN :minHours AND :maxHours ORDER BY pu.unitNumber")
    List<PathUnit> findByPathIdAndEstimatedHoursBetween(@Param("pathId") Integer pathId, @Param("minHours") Integer minHours, @Param("maxHours") Integer maxHours);

    /**
     * Check if a unit exists in a path
     */
    boolean existsByPathIdAndUnitId(Integer pathId, Integer unitId);
}
