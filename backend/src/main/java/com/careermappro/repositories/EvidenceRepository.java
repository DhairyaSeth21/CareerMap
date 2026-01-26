package com.careermappro.repositories;

import com.careermappro.entities.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Integer> {
    List<Evidence> findByUserId(Integer userId);
    List<Evidence> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
