package com.careermappro.controllers;

import com.careermappro.entities.*;
import com.careermappro.services.MultiLevelFrontierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/frontier")
public class MultiLevelFrontierController {

    @Autowired
    private MultiLevelFrontierService frontierService;

    /**
     * Level 1: Get all domains
     * GET /api/frontier/domains
     */
    @GetMapping("/domains")
    public ResponseEntity<List<Domain>> getAllDomains() {
        List<Domain> domains = frontierService.getAllDomains();
        return ResponseEntity.ok(domains);
    }

    /**
     * Level 1: Get specific domain
     * GET /api/frontier/domains/{domainId}
     */
    @GetMapping("/domains/{domainId}")
    public ResponseEntity<Domain> getDomainById(@PathVariable Integer domainId) {
        Optional<Domain> domain = frontierService.getDomainById(domainId);
        return domain.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Level 2: Get all career roles for a domain
     * GET /api/frontier/domains/{domainId}/roles
     */
    @GetMapping("/domains/{domainId}/roles")
    public ResponseEntity<List<CareerRole>> getRolesByDomain(@PathVariable Integer domainId) {
        List<CareerRole> roles = frontierService.getRolesByDomain(domainId);
        return ResponseEntity.ok(roles);
    }

    /**
     * Level 2: Get specific career role
     * GET /api/frontier/roles/{roleId}
     */
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<CareerRole> getRoleById(@PathVariable Integer roleId) {
        Optional<CareerRole> role = frontierService.getRoleById(roleId);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Level 3: Get deep path for a career role
     * GET /api/frontier/roles/{roleId}/path?userId={userId}
     */
    @GetMapping("/roles/{roleId}/path")
    public ResponseEntity<DeepPath> getDeepPathForRole(
            @PathVariable Integer roleId,
            @RequestParam(required = false) Integer userId) {
        Optional<DeepPath> path = frontierService.getDeepPathForRole(roleId, userId);
        return path.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Level 3: Get all deep paths for a career role
     * GET /api/frontier/roles/{roleId}/paths
     */
    @GetMapping("/roles/{roleId}/paths")
    public ResponseEntity<List<DeepPath>> getAllDeepPathsForRole(@PathVariable Integer roleId) {
        List<DeepPath> paths = frontierService.getAllDeepPathsForRole(roleId);
        return ResponseEntity.ok(paths);
    }

    /**
     * Level 3: Get specific deep path
     * GET /api/frontier/paths/{pathId}
     */
    @GetMapping("/paths/{pathId}")
    public ResponseEntity<DeepPath> getDeepPathById(@PathVariable Integer pathId) {
        Optional<DeepPath> path = frontierService.getDeepPathById(pathId);
        return path.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Level 4: Get all steps for a deep path
     * GET /api/frontier/paths/{pathId}/steps
     */
    @GetMapping("/paths/{pathId}/steps")
    public ResponseEntity<List<DeepPathStep>> getStepsForDeepPath(@PathVariable Integer pathId) {
        List<DeepPathStep> steps = frontierService.getStepsForDeepPath(pathId);
        return ResponseEntity.ok(steps);
    }

    /**
     * Level 4: Get steps for a specific week
     * GET /api/frontier/paths/{pathId}/steps/week/{weekNumber}
     */
    @GetMapping("/paths/{pathId}/steps/week/{weekNumber}")
    public ResponseEntity<List<DeepPathStep>> getStepsForWeek(
            @PathVariable Integer pathId,
            @PathVariable Integer weekNumber) {
        List<DeepPathStep> steps = frontierService.getStepsForWeek(pathId, weekNumber);
        return ResponseEntity.ok(steps);
    }

    /**
     * Helper endpoint: Get full hierarchy
     * GET /api/frontier/hierarchy?domainId={domainId}&roleId={roleId}&userId={userId}
     */
    @GetMapping("/hierarchy")
    public ResponseEntity<MultiLevelFrontierService.HierarchyView> getFullHierarchy(
            @RequestParam Integer domainId,
            @RequestParam Integer roleId,
            @RequestParam(required = false) Integer userId) {
        Optional<MultiLevelFrontierService.HierarchyView> hierarchy =
                frontierService.getFullHierarchy(domainId, roleId, userId);
        return hierarchy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
