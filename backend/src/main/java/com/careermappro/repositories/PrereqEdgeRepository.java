package com.careermappro.repositories;

import com.careermappro.entities.PrereqEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrereqEdgeRepository extends JpaRepository<PrereqEdge, Integer> {

    List<PrereqEdge> findByToSkillId(Integer toSkillId);

    List<PrereqEdge> findByFromSkillId(Integer fromSkillId);

    @Query("SELECT p FROM PrereqEdge p WHERE p.toSkillId IN :skillIds")
    List<PrereqEdge> findByToSkillIdIn(List<Integer> skillIds);

    @Query("SELECT p FROM PrereqEdge p WHERE p.fromSkillId IN :skillIds")
    List<PrereqEdge> findByFromSkillIdIn(List<Integer> skillIds);
}
