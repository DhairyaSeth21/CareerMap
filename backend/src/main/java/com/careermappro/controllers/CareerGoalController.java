package com.careermappro.controllers;

import com.careermappro.entities.CareerGoal;
import com.careermappro.services.CareerGoalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "https://levld.co", "https://www.levld.co"}, allowCredentials = "true")
@RequestMapping("/api/goals")
public class CareerGoalController {

    private final CareerGoalService service;

    public CareerGoalController(CareerGoalService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public List<CareerGoal> getUserGoals(@PathVariable Integer userId) {
        return service.getUserGoals(userId);
    }

    @PostMapping
    public CareerGoal create(@RequestBody CareerGoal goal) {
        return service.save(goal);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @PostMapping("/{goalId}/set-primary")
    public CareerGoal setPrimary(@PathVariable Integer goalId, @RequestParam Integer userId) {
        return service.setPrimaryGoal(goalId, userId);
    }
}
