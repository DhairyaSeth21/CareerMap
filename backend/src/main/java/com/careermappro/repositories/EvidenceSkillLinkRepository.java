package com.careermappro.repositories;

import com.careermappro.entities.EvidenceSkillLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceSkillLinkRepository extends JpaRepository<EvidenceSkillLink, Integer> {
    List<EvidenceSkillLink> findByEvidenceId(Integer evidenceId);
    List<EvidenceSkillLink> findBySkillId(Integer skillId);
}
