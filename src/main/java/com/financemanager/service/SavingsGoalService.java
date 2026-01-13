package com.financemanager.service;

import com.financemanager.dto.CreateGoalRequest;
import com.financemanager.dto.GoalResponse;
import com.financemanager.dto.GoalsResponse;
import com.financemanager.dto.UpdateGoalRequest;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.repository.SavingsGoalRepository;
import com.financemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for savings goal management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Create a new savings goal.
     *
     * @param request Create goal request
     * @param user Current user
     * @return GoalResponse with created goal
     * @throws IllegalArgumentException if target date is not in the future
     */
    public GoalResponse createGoal(CreateGoalRequest request, User user) {
        LocalDate targetDate = LocalDate.parse(request.getTargetDate(), DATE_FORMATTER);

        if (targetDate.isBefore(LocalDate.now()) || targetDate.isEqual(LocalDate.now())) {
            throw new IllegalArgumentException("Target date must be in the future");
        }

        LocalDate startDate = request.getStartDate() != null 
                ? LocalDate.parse(request.getStartDate(), DATE_FORMATTER) 
                : LocalDate.now();

        if (startDate.isAfter(targetDate)) {
            throw new IllegalArgumentException("Start date must be before target date");
        }

        SavingsGoal goal = SavingsGoal.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .targetDate(targetDate)
                .startDate(startDate)
                .user(user)
                .build();

        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return toResponse(savedGoal, user);
    }

    /**
     * Get all savings goals for a user.
     *
     * @param user Current user
     * @return GoalsResponse with list of goals
     */
    public GoalsResponse getAllGoals(User user) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserOrderByTargetDateAsc(user);
        List<GoalResponse> responses = goals.stream()
                .map(goal -> toResponse(goal, user))
                .collect(Collectors.toList());

        return GoalsResponse.builder()
                .goals(responses)
                .build();
    }

    /**
     * Get a specific savings goal.
     *
     * @param id Goal ID
     * @param user Current user
     * @return GoalResponse with goal details
     * @throws ResourceNotFoundException if goal not found
     * @throws ForbiddenException if goal belongs to another user
     */
    public GoalResponse getGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to access this goal");
        }

        return toResponse(goal, user);
    }

    /**
     * Update a savings goal.
     *
     * @param id Goal ID
     * @param request Update goal request
     * @param user Current user
     * @return GoalResponse with updated goal
     * @throws ResourceNotFoundException if goal not found
     * @throws ForbiddenException if goal belongs to another user
     */
    public GoalResponse updateGoal(Long id, UpdateGoalRequest request, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to update this goal");
        }

        if (request.getTargetAmount() != null && request.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            LocalDate newTargetDate = LocalDate.parse(request.getTargetDate(), DATE_FORMATTER);
            if (!newTargetDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Target date must be in the future");
            }
            if (goal.getStartDate() != null && goal.getStartDate().isAfter(newTargetDate)) {
                throw new IllegalArgumentException("Start date must be before target date");
            }
            goal.setTargetDate(newTargetDate);
        }

        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return toResponse(updatedGoal, user);
    }

    /**
     * Delete a savings goal.
     *
     * @param id Goal ID
     * @param user Current user
     * @throws ResourceNotFoundException if goal not found
     * @throws ForbiddenException if goal belongs to another user
     */
    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to delete this goal");
        }

        savingsGoalRepository.delete(goal);
    }

    /**
     * Calculate progress for a goal.
     *
     * @param goal SavingsGoal entity
     * @param user Current user
     * @return Calculated progress (income - expenses since goal start date)
     */
    private BigDecimal calculateProgress(SavingsGoal goal, User user) {
        List<Transaction> transactions = transactionRepository
                .findByUserAndDateRange(user, goal.getStartDate(), LocalDate.now());

        BigDecimal income = transactions.stream()
                .filter(t -> t.getCategory().getType() == CategoryType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = transactions.stream()
                .filter(t -> t.getCategory().getType() == CategoryType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return income.subtract(expenses);
    }

    /**
     * Convert SavingsGoal entity to GoalResponse.
     *
     * @param goal SavingsGoal entity
     * @param user Current user
     * @return GoalResponse
     */
    private GoalResponse toResponse(SavingsGoal goal, User user) {
        BigDecimal currentProgress = calculateProgress(goal, user);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);
        
        double progressPercentage = goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                ? currentProgress.divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue()
                : 0.0;

        if (progressPercentage > 100) {
            progressPercentage = 100.0;
        }

        return GoalResponse.builder()
                .id(goal.getId())
                .goalName(goal.getGoalName())
                .targetAmount(goal.getTargetAmount())
                .targetDate(goal.getTargetDate())
                .startDate(goal.getStartDate())
                .currentProgress(currentProgress)
                .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                .remainingAmount(remainingAmount.max(BigDecimal.ZERO))
                .build();
    }
}
