package com.careermappro.services;

import com.careermappro.entities.*;
import com.careermappro.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MultiLevelFrontierService {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CareerRoleRepository careerRoleRepository;

    @Autowired
    private DeepPathRepository deepPathRepository;

    @Autowired
    private DeepPathStepRepository deepPathStepRepository;

    /**
     * Level 1: Get all domains
     */
    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    /**
     * Level 1: Get specific domain by ID
     */
    public Optional<Domain> getDomainById(Integer domainId) {
        return domainRepository.findById(domainId);
    }

    /**
     * Level 2: Get all career roles for a specific domain
     */
    public List<CareerRole> getRolesByDomain(Integer domainId) {
        return careerRoleRepository.findByDomain_DomainId(domainId);
    }

    /**
     * Level 2: Get specific career role by ID
     */
    public Optional<CareerRole> getRoleById(Integer careerRoleId) {
        return careerRoleRepository.findById(careerRoleId);
    }

    /**
     * Level 3: Get deep path for a career role
     * For now returns the first deep path, can be enhanced to consider user's current progress
     */
    public Optional<DeepPath> getDeepPathForRole(Integer careerRoleId, Integer userId) {
        // TODO: Future enhancement - select path based on user's skill level
        // For now, return the first available path
        return deepPathRepository.findFirstByCareerRole_CareerRoleId(careerRoleId);
    }

    /**
     * Level 3: Get all deep paths for a career role
     */
    public List<DeepPath> getAllDeepPathsForRole(Integer careerRoleId) {
        return deepPathRepository.findByCareerRole_CareerRoleId(careerRoleId);
    }

    /**
     * Level 3: Get specific deep path by ID
     */
    public Optional<DeepPath> getDeepPathById(Integer deepPathId) {
        return deepPathRepository.findById(deepPathId);
    }

    /**
     * Level 4: Get all steps for a deep path (ordered by week and order_in_week)
     */
    public List<DeepPathStep> getStepsForDeepPath(Integer deepPathId) {
        return deepPathStepRepository.findByDeepPath_DeepPathIdOrderByWeekNumberAscOrderInWeekAsc(deepPathId);
    }

    /**
     * Level 4: Get steps for a specific week in a deep path
     */
    public List<DeepPathStep> getStepsForWeek(Integer deepPathId, Integer weekNumber) {
        return deepPathStepRepository.findByDeepPath_DeepPathIdAndWeekNumber(deepPathId, weekNumber);
    }

    /**
     * Helper: Get full hierarchy path from domain to skills
     * Returns data structure showing: Domain -> CareerRole -> DeepPath -> Skills
     */
    public Optional<HierarchyView> getFullHierarchy(Integer domainId, Integer careerRoleId, Integer userId) {
        Optional<Domain> domain = domainRepository.findById(domainId);
        if (domain.isEmpty()) {
            return Optional.empty();
        }

        Optional<CareerRole> role = careerRoleRepository.findById(careerRoleId);
        if (role.isEmpty() || !role.get().getDomain().getDomainId().equals(domainId)) {
            return Optional.empty();
        }

        Optional<DeepPath> path = getDeepPathForRole(careerRoleId, userId);
        if (path.isEmpty()) {
            return Optional.empty();
        }

        List<DeepPathStep> steps = getStepsForDeepPath(path.get().getDeepPathId());

        HierarchyView view = new HierarchyView();
        view.setDomain(domain.get());
        view.setCareerRole(role.get());
        view.setDeepPath(path.get());
        view.setSteps(steps);

        return Optional.of(view);
    }

    /**
     * DTO for full hierarchy view
     */
    public static class HierarchyView {
        private Domain domain;
        private CareerRole careerRole;
        private DeepPath deepPath;
        private List<DeepPathStep> steps;

        public Domain getDomain() { return domain; }
        public void setDomain(Domain domain) { this.domain = domain; }

        public CareerRole getCareerRole() { return careerRole; }
        public void setCareerRole(CareerRole careerRole) { this.careerRole = careerRole; }

        public DeepPath getDeepPath() { return deepPath; }
        public void setDeepPath(DeepPath deepPath) { this.deepPath = deepPath; }

        public List<DeepPathStep> getSteps() { return steps; }
        public void setSteps(List<DeepPathStep> steps) { this.steps = steps; }
    }
}
