package com.financemanager.controller;

import com.financemanager.dto.CreateGoalRequest;
import com.financemanager.dto.GoalResponse;
import com.financemanager.dto.GoalsResponse;
import com.financemanager.dto.MessageResponse;
import com.financemanager.dto.UpdateGoalRequest;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for savings goal management endpoints.
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;
    private final AuthenticationService authenticationService;

    /**
     * Create a new savings goal.
     *
     * @param request Create goal request
     * @return GoalResponse with created goal
     */
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        var user = authenticationService.getCurrentUser();
        GoalResponse response = savingsGoalService.createGoal(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all savings goals for the user.
     *
     * @return GoalsResponse with all goals
     */
    @GetMapping
    public ResponseEntity<GoalsResponse> getAllGoals() {
        var user = authenticationService.getCurrentUser();
        GoalsResponse response = savingsGoalService.getAllGoals(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific savings goal.
     *
     * @param id Goal ID
     * @return GoalResponse with goal details
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {
        var user = authenticationService.getCurrentUser();
        GoalResponse response = savingsGoalService.getGoal(id, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a savings goal.
     *
     * @param id Goal ID
     * @param request Update goal request
     * @return GoalResponse with updated goal
     */
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request) {
        var user = authenticationService.getCurrentUser();
        GoalResponse response = savingsGoalService.updateGoal(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a savings goal.
     *
     * @param id Goal ID
     * @return MessageResponse with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteGoal(@PathVariable Long id) {
        var user = authenticationService.getCurrentUser();
        savingsGoalService.deleteGoal(id, user);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Goal deleted successfully")
                .build());
    }
}
