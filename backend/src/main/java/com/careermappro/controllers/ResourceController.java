package com.careermappro.controllers;

import com.careermappro.models.CuratedResource;
import com.careermappro.services.ResourceSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for learning resource management
 * Handles resource selection, rating, and discovery
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceSelectionService resourceSelectionService;

    /**
     * GET /api/resources/node/{nodeId}?userId={userId}
     * Get personalized resources for a node
     */
    @GetMapping("/node/{nodeId}")
    public ResponseEntity<Map<String, Object>> getResourcesForNode(
            @PathVariable Integer nodeId,
            @RequestParam Integer userId) {

        System.out.println("[RESOURCE-API] GET resources for nodeId=" + nodeId + ", userId=" + userId);

        try {
            List<CuratedResource> resources = resourceSelectionService.getResourcesForNode(userId, nodeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resources", resources);
            response.put("count", resources.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[RESOURCE-API] Error getting resources: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * POST /api/resources/rate
     * Rate a resource
     * Body: { userId, resourceId, rating, helpful, feedback? }
     */
    @PostMapping("/rate")
    public ResponseEntity<Map<String, Object>> rateResource(@RequestBody Map<String, Object> request) {
        System.out.println("[RESOURCE-API] POST rate resource: " + request);

        try {
            Integer userId = (Integer) request.get("userId");
            Integer resourceId = (Integer) request.get("resourceId");
            Float rating = ((Number) request.get("rating")).floatValue();
            Boolean helpful = (Boolean) request.getOrDefault("helpful", true);
            String feedback = (String) request.get("feedback");

            resourceSelectionService.rateResource(userId, resourceId, rating, helpful, feedback);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rating saved successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[RESOURCE-API] Error rating resource: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * POST /api/resources/find-different
     * Find a different resource (when user clicks "Find different")
     * Body: { userId, nodeId, currentResourceId }
     */
    @PostMapping("/find-different")
    public ResponseEntity<Map<String, Object>> findDifferentResource(@RequestBody Map<String, Object> request) {
        System.out.println("[RESOURCE-API] POST find different resource: " + request);

        try {
            Integer userId = (Integer) request.get("userId");
            Integer nodeId = (Integer) request.get("nodeId");
            Integer currentResourceId = (Integer) request.get("currentResourceId");

            List<CuratedResource> resources =
                resourceSelectionService.findDifferentResource(userId, nodeId, currentResourceId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resources", resources);
            response.put("message", "Found " + resources.size() + " alternative resources");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[RESOURCE-API] Error finding different resource: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * POST /api/resources/discover
     * Manually trigger resource discovery for a node
     * Body: { nodeId }
     */
    @PostMapping("/discover")
    public ResponseEntity<Map<String, Object>> discoverResources(@RequestBody Map<String, Object> request) {
        System.out.println("[RESOURCE-API] POST discover resources: " + request);

        try {
            Integer nodeId = (Integer) request.get("nodeId");

            List<CuratedResource> resources = resourceSelectionService.discoverNewResources(nodeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resources", resources);
            response.put("message", "Discovered " + resources.size() + " new resources");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("[RESOURCE-API] Error discovering resources: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
