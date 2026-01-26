package com.careermappro.controllers;

import com.careermappro.services.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"}, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/register
     * Register a new user
     * Body: { name, email, password }
     */
    @PostMapping("/api/v1/auth/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        return authService.register(name, email, password);
    }

    /**
     * POST /api/v1/auth/login
     * Login user
     * Body: { email, password }
     */
    @PostMapping("/api/v1/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return authService.login(email, password);
    }

    /**
     * GET /api/v1/me
     * Get current user details from JWT token
     * Header: Authorization: Bearer <token>
     */
    @GetMapping("/api/v1/me")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Missing or invalid Authorization header");
            return error;
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return authService.getUserFromToken(token);
    }

    /**
     * POST /api/v1/auth/verify
     * Verify JWT token
     * Body: { token }
     */
    @PostMapping("/api/v1/auth/verify")
    public Map<String, Object> verifyToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        return authService.getUserFromToken(token);
    }

    // Legacy endpoint - keep for backward compatibility temporarily
    @PostMapping("/api/auth/register")
    public Map<String, Object> registerLegacy(@RequestBody Map<String, String> request) {
        return register(request);
    }

    @PostMapping("/api/auth/login")
    public Map<String, Object> loginLegacy(@RequestBody Map<String, String> request) {
        return login(request);
    }

    @GetMapping("/api/auth/user/{userId}")
    public Map<String, Object> getUserLegacy(@PathVariable Integer userId) {
        return authService.getUserById(userId);
    }
}
