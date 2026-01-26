package com.careermappro.repositories;

import com.careermappro.models.CuratedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuratedResourceRepository extends JpaRepository<CuratedResource, Integer> {

    /**
     * Find resources by type (video, article, documentation, etc.)
     */
    List<CuratedResource> findByType(String type);

    /**
     * Find resources by source (youtube, mdn, official_docs, etc.)
     */
    List<CuratedResource> findBySource(String source);

    /**
     * Find resources with quality score above threshold
     */
    List<CuratedResource> findByAvgQualityScoreGreaterThanEqual(Float minScore);

    /**
     * Find all resources linked to a specific node
     */
    @Query(value = """
        SELECT cr.* FROM curated_resources cr
        INNER JOIN node_resources nr ON cr.resource_id = nr.resource_id
        WHERE nr.node_id = :nodeId
        ORDER BY nr.priority ASC, cr.avg_quality_score DESC
        """, nativeQuery = true)
    List<CuratedResource> findByNodeId(@Param("nodeId") Integer nodeId);

    /**
     * Find resources by tags (JSON search)
     */
    @Query(value = """
        SELECT * FROM curated_resources
        WHERE JSON_CONTAINS(tags, JSON_QUOTE(:tag))
        ORDER BY avg_quality_score DESC
        """, nativeQuery = true)
    List<CuratedResource> findByTag(@Param("tag") String tag);

    /**
     * Find top-rated resources
     */
    @Query(value = """
        SELECT * FROM curated_resources
        WHERE total_ratings >= :minRatings
        ORDER BY avg_quality_score DESC, total_ratings DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<CuratedResource> findTopRated(@Param("minRatings") Integer minRatings, @Param("limit") Integer limit);
}
