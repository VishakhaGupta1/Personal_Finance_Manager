package com.financemanager.service;

import com.financemanager.dto.UpdateGoalRequest;
import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.User;
import com.financemanager.repository.SavingsGoalRepository;
import com.financemanager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceEdgeTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User user;
    private SavingsGoal goal;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("u").build();
        goal = SavingsGoal.builder()
                .id(1L)
                .goalName("G")
                .targetAmount(new BigDecimal("1000"))
                .targetDate(LocalDate.now().plusMonths(2))
                .startDate(LocalDate.now())
                .user(user)
                .build();
    }

    @Test
    void updateGoal_negativeTargetAmount_isIgnored() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenAnswer(i -> i.getArgument(0));

        UpdateGoalRequest req = UpdateGoalRequest.builder().targetAmount(new BigDecimal("-5")).build();
        var resp = savingsGoalService.updateGoal(1L, req, user);

        assertEquals(new BigDecimal("1000"), resp.getTargetAmount());
    }
}
