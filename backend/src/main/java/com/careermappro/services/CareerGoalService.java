package com.careermappro.services;

import com.careermappro.entities.CareerGoal;
import com.careermappro.repositories.CareerGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerGoalService {

    private final CareerGoalRepository repo;
    private final AnalyticsService analyticsService;

    public CareerGoalService(CareerGoalRepository repo, AnalyticsService analyticsService) {
        this.repo = repo;
        this.analyticsService = analyticsService;
    }

    public List<CareerGoal> getUserGoals(Integer userId) {
        return repo.findByUserId(userId);
    }

    public CareerGoal save(CareerGoal goal) {
        // Auto-calculate readiness based on user's skills and goal description
        if (goal.getUserId() != null && goal.getGoal() != null) {
            double calculatedReadiness = analyticsService.calculateGoalReadiness(
                goal.getUserId(),
                goal.getGoal()
            );
            goal.setReadiness(calculatedReadiness);
        }

        return repo.save(goal);
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }

    public CareerGoal setPrimaryGoal(Integer goalId, Integer userId) {
        // First, unset all primary goals for this user
        List<CareerGoal> userGoals = repo.findByUserId(userId);
        for (CareerGoal g : userGoals) {
            if (g.getIsPrimary() != null && g.getIsPrimary()) {
                g.setIsPrimary(false);
                repo.save(g);
            }
        }

        // Now set the selected goal as primary
        CareerGoal primaryGoal = repo.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Goal not found: " + goalId));
        primaryGoal.setIsPrimary(true);
        return repo.save(primaryGoal);
    }
}
