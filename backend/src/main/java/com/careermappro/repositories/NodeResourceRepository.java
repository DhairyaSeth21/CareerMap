package com.careermappro.repositories;

import com.careermappro.models.NodeResource;
import com.careermappro.models.NodeResourceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeResourceRepository extends JpaRepository<NodeResource, NodeResourceId> {

    /**
     * Find all resources for a specific node, ordered by priority
     */
    List<NodeResource> findByNodeIdOrderByPriorityAsc(Integer nodeId);

    /**
     * Find all nodes using a specific resource
     */
    List<NodeResource> findByResourceId(Integer resourceId);

    /**
     * Check if a node-resource mapping exists
     */
    boolean existsByNodeIdAndResourceId(Integer nodeId, Integer resourceId);

    /**
     * Delete a specific node-resource mapping
     */
    void deleteByNodeIdAndResourceId(Integer nodeId, Integer resourceId);

    /**
     * Get the next priority number for a node (for adding new resources)
     */
    @Query(value = """
        SELECT COALESCE(MAX(priority), 0) + 1
        FROM node_resources
        WHERE node_id = :nodeId
        """, nativeQuery = true)
    Integer getNextPriority(@Param("nodeId") Integer nodeId);
}
