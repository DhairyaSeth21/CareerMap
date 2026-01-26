package com.careermappro.repositories;

import com.careermappro.entities.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Integer> {
    List<Opportunity> findByUserId(Integer userId);
    Optional<Opportunity> findByUserIdAndJdHash(Integer userId, String jdHash);
}
