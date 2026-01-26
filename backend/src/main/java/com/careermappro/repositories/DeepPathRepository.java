package com.careermappro.repositories;

import com.careermappro.entities.DeepPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeepPathRepository extends JpaRepository<DeepPath, Integer> {
    List<DeepPath> findByCareerRole_CareerRoleId(Integer careerRoleId);
    Optional<DeepPath> findFirstByCareerRole_CareerRoleId(Integer careerRoleId);
}
