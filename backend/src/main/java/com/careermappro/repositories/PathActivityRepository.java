package com.careermappro.repositories;

import com.careermappro.entities.PathActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PathActivityRepository extends JpaRepository<PathActivity, Integer> {

    /**
     * Find all activities for a specific user
     */
    List<PathActivity> findByUserId(Integer userId);

    /**
     * Find all activities for a specific learning path
     */
    List<PathActivity> findByPathId(Integer pathId);

    /**
     * Find all activities for a specific user on a specific path
     */
    List<PathActivity> findByUserIdAndPathId(Integer userId, Integer pathId);

    /**
     * Find activities for a specific step
     */
    List<PathActivity> findByStepId(Integer stepId);

    /**
     * Find activities by type
     */
    List<PathActivity> findByActivityType(PathActivity.ActivityType activityType);

    /**
     * Find user activities ordered by creation date (most recent first)
     */
    List<PathActivity> findByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * Find user activities on a path ordered by creation date (most recent first)
     */
    List<PathActivity> findByUserIdAndPathIdOrderByCreatedAtDesc(Integer userId, Integer pathId);

    /**
     * Find activities by user and type
     */
    List<PathActivity> findByUserIdAndActivityType(Integer userId, PathActivity.ActivityType activityType);

    /**
     * Find activities on a path by type
     */
    List<PathActivity> findByPathIdAndActivityType(Integer pathId, PathActivity.ActivityType activityType);

    /**
     * Find activities within a date range
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.createdAt BETWEEN :startDate AND :endDate ORDER BY pa.createdAt DESC")
    List<PathActivity> findActivitiesByDateRange(@Param("userId") Integer userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find path started activities for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.activityType = 'PATH_STARTED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findPathStartedActivities(@Param("userId") Integer userId);

    /**
     * Find unit unlocked activities for a user on a path
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.pathId = :pathId AND pa.activityType = 'UNIT_UNLOCKED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findUnitUnlockedActivities(@Param("userId") Integer userId, @Param("pathId") Integer pathId);

    /**
     * Find unit completed activities for a user on a path
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.pathId = :pathId AND pa.activityType = 'UNIT_COMPLETED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findUnitCompletedActivities(@Param("userId") Integer userId, @Param("pathId") Integer pathId);

    /**
     * Find evidence submitted activities for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.activityType = 'EVIDENCE_SUBMITTED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findEvidenceSubmittedActivities(@Param("userId") Integer userId);

    /**
     * Find quiz taken activities for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.activityType = 'QUIZ_TAKEN' ORDER BY pa.createdAt DESC")
    List<PathActivity> findQuizTakenActivities(@Param("userId") Integer userId);

    /**
     * Find milestone reached activities for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.activityType = 'MILESTONE_REACHED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findMilestoneActivities(@Param("userId") Integer userId);

    /**
     * Find resource accessed activities for a user on a path
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.pathId = :pathId AND pa.activityType = 'RESOURCE_ACCESSED' ORDER BY pa.createdAt DESC")
    List<PathActivity> findResourceAccessedActivities(@Param("userId") Integer userId, @Param("pathId") Integer pathId);

    /**
     * Count activities for a user
     */
    long countByUserId(Integer userId);

    /**
     * Count activities for a path
     */
    long countByPathId(Integer pathId);

    /**
     * Count specific activity types for a user
     */
    long countByUserIdAndActivityType(Integer userId, PathActivity.ActivityType activityType);

    /**
     * Count specific activity types for a path
     */
    long countByPathIdAndActivityType(Integer pathId, PathActivity.ActivityType activityType);

    /**
     * Find most recent activity for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId ORDER BY pa.createdAt DESC LIMIT 1")
    Optional<PathActivity> findMostRecentActivity(@Param("userId") Integer userId);

    /**
     * Find most recent activity on a path for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.pathId = :pathId ORDER BY pa.createdAt DESC LIMIT 1")
    Optional<PathActivity> findMostRecentPathActivity(@Param("userId") Integer userId, @Param("pathId") Integer pathId);

    /**
     * Check if user has started a path
     */
    boolean existsByUserIdAndPathIdAndActivityType(Integer userId, Integer pathId, PathActivity.ActivityType activityType);

    /**
     * Find activities by step and type
     */
    List<PathActivity> findByStepIdAndActivityType(Integer stepId, PathActivity.ActivityType activityType);

    /**
     * Find recent activities across all users for a path (for analytics)
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.pathId = :pathId AND pa.createdAt >= :sinceDate ORDER BY pa.createdAt DESC")
    List<PathActivity> findRecentActivitiesOnPath(@Param("pathId") Integer pathId, @Param("sinceDate") LocalDateTime sinceDate);

    /**
     * Count milestone activities for a user
     */
    @Query("SELECT COUNT(pa) FROM PathActivity pa WHERE pa.userId = :userId AND pa.activityType = 'MILESTONE_REACHED'")
    long countMilestonesByUser(@Param("userId") Integer userId);

    /**
     * Get activity statistics for a path
     */
    @Query("SELECT pa.activityType, COUNT(pa) FROM PathActivity pa WHERE pa.pathId = :pathId GROUP BY pa.activityType")
    List<Object[]> getActivityStatisticsByPath(@Param("pathId") Integer pathId);

    /**
     * Find step specific activities for a user
     */
    @Query("SELECT pa FROM PathActivity pa WHERE pa.userId = :userId AND pa.stepId = :stepId ORDER BY pa.createdAt DESC")
    List<PathActivity> findActivitiesByUserAndStep(@Param("userId") Integer userId, @Param("stepId") Integer stepId);
}
