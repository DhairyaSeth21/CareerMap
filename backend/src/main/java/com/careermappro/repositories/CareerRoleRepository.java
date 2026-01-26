package com.careermappro.repositories;

import com.careermappro.entities.CareerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRoleRepository extends JpaRepository<CareerRole, Integer> {
    List<CareerRole> findByDomain_DomainId(Integer domainId);
    Optional<CareerRole> findByNameAndDomain_DomainId(String name, Integer domainId);
}
