package com.careermappro.repositories;

import com.careermappro.models.UserResourceRating;
import com.careermappro.models.UserResourceRatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserResourceRatingRepository extends JpaRepository<UserResourceRating, UserResourceRatingId> {

    /**
     * Find a specific user's rating for a resource
     */
    Optional<UserResourceRating> findByUserIdAndResourceId(Integer userId, Integer resourceId);

    /**
     * Find all ratings by a user
     */
    List<UserResourceRating> findByUserId(Integer userId);

    /**
     * Find all ratings for a resource
     */
    List<UserResourceRating> findByResourceId(Integer resourceId);

    /**
     * Find resources user marked as NOT helpful (to exclude from recommendations)
     */
    List<UserResourceRating> findByUserIdAndHelpfulFalse(Integer userId);

    /**
     * Find resources user rated below a threshold (to exclude)
     */
    List<UserResourceRating> findByUserIdAndRatingLessThan(Integer userId, Float minRating);

    /**
     * Find user's preferred resource types based on high ratings
     */
    @Query(value = """
        SELECT cr.type, AVG(urr.rating) as avg_rating, COUNT(*) as count
        FROM user_resource_ratings urr
        INNER JOIN curated_resources cr ON urr.resource_id = cr.resource_id
        WHERE urr.user_id = :userId AND urr.rating >= 4.0
        GROUP BY cr.type
        ORDER BY avg_rating DESC, count DESC
        LIMIT 1
        """, nativeQuery = true)
    Object[] findPreferredResourceType(@Param("userId") Integer userId);

    /**
     * Calculate average rating for a resource
     */
    @Query(value = """
        SELECT AVG(rating) FROM user_resource_ratings
        WHERE resource_id = :resourceId
        """, nativeQuery = true)
    Float calculateAvgRating(@Param("resourceId") Integer resourceId);

    /**
     * Count total ratings for a resource
     */
    @Query(value = """
        SELECT COUNT(*) FROM user_resource_ratings
        WHERE resource_id = :resourceId
        """, nativeQuery = true)
    Integer countRatings(@Param("resourceId") Integer resourceId);
}
