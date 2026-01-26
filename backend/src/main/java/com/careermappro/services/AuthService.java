package com.careermappro.services;

import com.careermappro.entities.User;
import com.careermappro.repositories.UserRepository;
import com.careermappro.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Register a new user
     */
    public Map<String, Object> register(String name, String email, String password) {
        Map<String, Object> response = new HashMap<>();

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return response;
        }

        // Validate password
        if (password == null || password.length() < 6) {
            response.put("success", false);
            response.put("message", "Password must be at least 6 characters");
            return response;
        }

        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(name, email);
        user.setPassword(hashedPassword);

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getName());

        response.put("success", true);
        response.put("message", "User registered successfully");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("token", token);

        return response;
    }

    /**
     * Login user
     */
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        User user = userOpt.get();

        // Verify password with BCrypt
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getName());

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("token", token);

        return response;
    }

    /**
     * Get user by ID
     */
    public Map<String, Object> getUserById(Integer userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }

        User user = userOpt.get();

        response.put("success", true);
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("xp", user.getXp());
        response.put("level", user.getLevel());
        response.put("streak", user.getStreak());

        return response;
    }

    /**
     * Validate JWT token and get user info
     */
    public Map<String, Object> getUserFromToken(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!jwtUtil.isTokenValid(token)) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return response;
            }

            Integer userId = jwtUtil.extractUserId(token);
            return getUserById(userId);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Invalid token");
            return response;
        }
    }
}
