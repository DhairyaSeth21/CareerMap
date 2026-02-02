package com.careermappro.controllers;

import com.careermappro.entities.Session;
import com.careermappro.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * SessionController
 * REST API for session management (PROBE/BUILD/PROVE/APPLY)
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    /**
     * Propose a new PROBE session for a user on a specific skill
     * POST /api/sessions/propose
     */
    @PostMapping("/propose")
    public ResponseEntity<?> proposeSession(
        @RequestParam Integer userId,
        @RequestParam Integer skillNodeId
    ) {
        try {
            Session session = sessionService.proposeProbeSession(userId, skillNodeId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Start a proposed session
     * POST /api/sessions/{sessionId}/start
     */
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<?> startSession(@PathVariable Integer sessionId) {
        try {
            Session session = sessionService.startSession(sessionId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Complete a session with score
     * POST /api/sessions/{sessionId}/complete
     */
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<?> completeSession(
        @PathVariable Integer sessionId,
        @RequestParam BigDecimal score,
        @RequestParam(required = false) Integer quizId
    ) {
        try {
            Session session = sessionService.completeSession(sessionId, score, quizId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get current proposed session for user
     * GET /api/sessions/current?userId={userId}
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSession(@RequestParam Integer userId) {
        Optional<Session> session = sessionService.getCurrentProposedSession(userId);
        if (session.isPresent()) {
            return ResponseEntity.ok(session.get());
        } else {
            return ResponseEntity.ok().body("{\"message\": \"No active session\"}");
        }
    }

    /**
     * Get all sessions for user
     * GET /api/sessions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getUserSessions(@PathVariable Integer userId) {
        List<Session> sessions = sessionService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Expire old sessions (maintenance endpoint)
     * POST /api/sessions/expire-old
     */
    @PostMapping("/expire-old")
    public ResponseEntity<String> expireOldSessions() {
        sessionService.expireOldSessions();
        return ResponseEntity.ok("Old sessions expired");
    }
}
