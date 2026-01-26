package com.careermappro.repositories;

import com.careermappro.entities.SkillNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillNodeRepository extends JpaRepository<SkillNode, Integer> {

    Optional<SkillNode> findByCanonicalName(String canonicalName);

    List<SkillNode> findByDomain(String domain);

    @Query("SELECT s FROM SkillNode s WHERE s.canonicalName IN :names")
    List<SkillNode> findByCanonicalNameIn(List<String> names);

    @Query(value = "SELECT * FROM skill_nodes WHERE JSON_CONTAINS(aliases, JSON_QUOTE(?1))", nativeQuery = true)
    List<SkillNode> findByAlias(String alias);

    /**
     * Find all skills that are required for a specific role
     * Joins with role_skill table to get role-specific skills
     */
    @Query(value = """
        SELECT sn.* FROM skill_nodes sn
        INNER JOIN role_skill rs ON sn.skill_node_id = rs.skill_id
        WHERE rs.role_id = :roleId
        ORDER BY rs.weight DESC
        """, nativeQuery = true)
    List<SkillNode> findSkillsByRoleId(Integer roleId);
}
