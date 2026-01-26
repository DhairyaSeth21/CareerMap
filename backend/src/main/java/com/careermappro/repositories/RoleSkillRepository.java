package com.careermappro.repositories;

import com.careermappro.entities.RoleSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleSkillRepository extends JpaRepository<RoleSkill, Integer> {
    List<RoleSkill> findByRoleId(Integer roleId);
}
