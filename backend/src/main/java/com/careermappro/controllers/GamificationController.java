package com.careermappro.controllers;

import com.careermappro.entities.*;
import com.careermappro.repositories.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
@RequestMapping("/api/gamification")
public class GamificationController {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final QuizRepository quizRepository;
    private final UserSkillRepository userSkillRepository;
    private final CareerGoalRepository careerGoalRepository;

    public GamificationController(
        UserRepository userRepository,
        AchievementRepository achievementRepository,
        UserAchievementRepository userAchievementRepository,
        QuizRepository quizRepository,
        UserSkillRepository userSkillRepository,
        CareerGoalRepository careerGoalRepository
    ) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.quizRepository = quizRepository;
        this.userSkillRepository = userSkillRepository;
        this.careerGoalRepository = careerGoalRepository;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserGamificationData(@PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Map.of("error", "User not found");
        }

        // Get user's achievements
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);
        List<Achievement> allAchievements = achievementRepository.findAll();

        // Calculate progress for each achievement
        List<Map<String, Object>> achievementsData = allAchievements.stream().map(achievement -> {
            UserAchievement userAch = userAchievements.stream()
                .filter(ua -> ua.getAchievement().getId().equals(achievement.getId()))
                .findFirst()
                .orElse(null);

            Map<String, Object> achData = new HashMap<>();
            achData.put("id", achievement.getId());
            achData.put("name", achievement.getName());
            achData.put("description", achievement.getDescription());
            achData.put("icon", achievement.getIcon());
            achData.put("xpReward", achievement.getXpReward());
            achData.put("category", achievement.getCategory());

            if (userAch != null && userAch.isUnlocked()) {
                achData.put("unlocked", true);
                achData.put("unlockedAt", userAch.getUnlockedAt().toString());
                achData.put("progress", 100);
            } else {
                achData.put("unlocked", false);
                int progress = calculateProgress(userId, achievement);
                achData.put("progress", progress);
            }

            return achData;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("xp", user.getXp());
        result.put("level", user.getLevel());
        result.put("streak", user.getStreak());
        result.put("achievements", achievementsData);
        result.put("totalBadges", (int) achievementsData.stream().filter(a -> (boolean) a.get("unlocked")).count());

        return result;
    }

    @PostMapping("/update-xp/{userId}")
    public Map<String, Object> updateUserXP(@PathVariable Integer userId, @RequestBody Map<String, Integer> request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Map.of("error", "User not found");
        }

        Integer xpToAdd = request.getOrDefault("xp", 0);
        user.setXp(user.getXp() + xpToAdd);

        // Calculate new level (100 XP per level)
        int newLevel = (user.getXp() / 100) + 1;
        user.setLevel(newLevel);

        userRepository.save(user);

        return Map.of(
            "xp", user.getXp(),
            "level", user.getLevel(),
            "message", "XP updated successfully"
        );
    }

    @PostMapping("/update-streak/{userId}")
    public Map<String, Object> updateUserStreak(@PathVariable Integer userId, @RequestBody Map<String, Integer> request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Map.of("error", "User not found");
        }

        Integer streak = request.get("streak");
        if (streak != null) {
            user.setStreak(streak);
            userRepository.save(user);
        }

        return Map.of("streak", user.getStreak());
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
            .sorted((u1, u2) -> Integer.compare(u2.getXp(), u1.getXp()))
            .limit(10)
            .map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", user.getName());
                userData.put("xp", user.getXp());
                userData.put("level", user.getLevel());
                userData.put("streak", user.getStreak());
                return userData;
            })
            .collect(Collectors.toList());
    }

    private int calculateProgress(Integer userId, Achievement achievement) {
        String category = achievement.getCategory();
        String criteria = achievement.getUnlockCriteria();

        try {
            int target = Integer.parseInt(criteria);
            int current = 0;

            switch (category) {
                case "quiz":
                    current = quizRepository.findAll().size(); // Simplified - should count user's completed quizzes
                    break;
                case "skill":
                    current = userSkillRepository.findByUserId(userId).size();
                    break;
                case "streak":
                    User user = userRepository.findById(userId).orElse(null);
                    current = user != null ? user.getStreak() : 0;
                    break;
                case "goal":
                    current = careerGoalRepository.findByUserId(userId).size();
                    break;
            }

            return Math.min(100, (int) ((current / (double) target) * 100));
        } catch (Exception e) {
            return 0;
        }
    }
}
