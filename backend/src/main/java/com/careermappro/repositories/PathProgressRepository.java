package com.careermappro.repositories;

import com.careermappro.entities.PathProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathProgressRepository extends JpaRepository<PathProgress, Integer> {

    /**
     * Find progress record for a specific user and path
     */
    Optional<PathProgress> findByPathIdAndUserId(Integer pathId, Integer userId);

    /**
     * Find all progress records for a user
     */
    List<PathProgress> findByUserId(Integer userId);

    /**
     * Find all progress records for a specific path
     */
    List<PathProgress> findByPathId(Integer pathId);

    /**
     * Find progress records ordered by last activity (most recent first)
     */
    List<PathProgress> findByUserIdOrderByLastActivityAtDesc(Integer userId);

    /**
     * Find progress records for a user ordered by overall progress (highest first)
     */
    List<PathProgress> findByUserIdOrderByOverallProgressDesc(Integer userId);

    /**
     * Find paths where user has completed all units
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.totalUnits > 0 AND pp.completedUnits = pp.totalUnits")
    List<PathProgress> findCompletedPaths(@Param("userId") Integer userId);

    /**
     * Find paths in progress (started but not all units completed)
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.completedUnits < pp.totalUnits AND pp.totalUnits > 0 ORDER BY pp.lastActivityAt DESC")
    List<PathProgress> findInProgressPaths(@Param("userId") Integer userId);

    /**
     * Find paths not yet started (0 units completed)
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.completedUnits = 0")
    List<PathProgress> findNotStartedPaths(@Param("userId") Integer userId);

    /**
     * Find progress records with overall progress above a threshold
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.overallProgress >= :minProgress ORDER BY pp.overallProgress DESC")
    List<PathProgress> findByProgressAboveThreshold(@Param("userId") Integer userId, @Param("minProgress") java.math.BigDecimal minProgress);

    /**
     * Find progress records with specific current unit
     */
    Optional<PathProgress> findByUserIdAndCurrentUnitId(Integer userId, Integer currentUnitId);

    /**
     * Find progress records with significant time investment
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.totalTimeMinutes >= :minMinutes ORDER BY pp.totalTimeMinutes DESC")
    List<PathProgress> findByTimeInvestment(@Param("userId") Integer userId, @Param("minMinutes") Integer minMinutes);

    /**
     * Find progress records with evidence submission
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.evidenceSubmitted > 0 ORDER BY pp.evidenceSubmitted DESC")
    List<PathProgress> findWithEvidenceSubmission(@Param("userId") Integer userId);

    /**
     * Find progress records with quiz completions
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.quizzesCompleted > 0 ORDER BY pp.quizzesCompleted DESC")
    List<PathProgress> findWithQuizCompletion(@Param("userId") Integer userId);

    /**
     * Calculate average progress across all paths for a user
     */
    @Query("SELECT AVG(pp.overallProgress) FROM PathProgress pp WHERE pp.userId = :userId")
    Optional<java.math.BigDecimal> getAverageProgress(@Param("userId") Integer userId);

    /**
     * Find total time spent across all paths for a user
     */
    @Query("SELECT SUM(pp.totalTimeMinutes) FROM PathProgress pp WHERE pp.userId = :userId")
    Optional<Long> getTotalTimeSpent(@Param("userId") Integer userId);

    /**
     * Count total paths with activity for a user
     */
    long countByUserIdAndTotalUnitsGreaterThan(Integer userId, Integer units);

    /**
     * Find paths with highest time investment
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId ORDER BY pp.totalTimeMinutes DESC LIMIT :limit")
    List<PathProgress> findMostTimeInvestedPaths(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * Find paths with most units completed
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId ORDER BY pp.completedUnits DESC LIMIT :limit")
    List<PathProgress> findMostCompletedUnits(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * Check if user has made progress on a path
     */
    boolean existsByPathIdAndUserIdAndCompletedUnitsGreaterThan(Integer pathId, Integer userId, Integer units);

    /**
     * Find progress records updated recently
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId AND pp.updatedAt >= :sinceDate ORDER BY pp.updatedAt DESC")
    List<PathProgress> findRecentlyUpdated(@Param("userId") Integer userId, @Param("sinceDate") java.time.LocalDateTime sinceDate);

    /**
     * Find paths sorted by completion percentage
     */
    @Query("SELECT pp FROM PathProgress pp WHERE pp.userId = :userId ORDER BY (pp.completedUnits * 100.0 / pp.totalUnits) DESC")
    List<PathProgress> findByUserIdOrderByCompletionPercentage(@Param("userId") Integer userId);

    /**
     * Get completion statistics for a path
     */
    @Query("SELECT COUNT(pp), AVG(pp.overallProgress) FROM PathProgress pp WHERE pp.pathId = :pathId")
    Optional<Object[]> getPathCompletionStats(@Param("pathId") Integer pathId);
}
