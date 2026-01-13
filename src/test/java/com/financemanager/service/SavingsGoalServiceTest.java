package com.financemanager.service;

import com.financemanager.dto.CreateGoalRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SavingsGoalService.
 */
@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SavingsGoalService savingsGoalService;

    private User user;
    private SavingsGoal goal;
    private CreateGoalRequest createRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        goal = SavingsGoal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(BigDecimal.valueOf(5000))
                .targetDate(LocalDate.now().plusMonths(12))
                .startDate(LocalDate.now())
                .user(user)
                .build();

        createRequest = CreateGoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(BigDecimal.valueOf(5000))
                .targetDate(LocalDate.now().plusMonths(12).toString())
                .build();
    }

    @Test
    void testCreateGoalSuccess() {
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(goal);
        when(transactionRepository.findByUserAndDateRange(any(), any(), any())).thenReturn(new ArrayList<>());

        var response = savingsGoalService.createGoal(createRequest, user);

        assertNotNull(response);
        assertEquals("Emergency Fund", response.getGoalName());
        assertEquals(BigDecimal.valueOf(5000), response.getTargetAmount());
        verify(savingsGoalRepository, times(1)).save(any(SavingsGoal.class));
    }

    @Test
    void testCreateGoalPastDate() {
        createRequest.setTargetDate(LocalDate.now().minusDays(1).toString());

        assertThrows(IllegalArgumentException.class, () -> savingsGoalService.createGoal(createRequest, user));
    }

    @Test
    void testGetGoalNotFound() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> savingsGoalService.getGoal(1L, user));
    }

    @Test
    void testGetGoalForbidden() {
        User otherUser = User.builder()
                .id(2L)
                .username("other@example.com")
                .build();

        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(ForbiddenException.class, () -> savingsGoalService.getGoal(1L, otherUser));
    }

    @Test
    void testDeleteGoalSuccess() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));

        savingsGoalService.deleteGoal(1L, user);

        verify(savingsGoalRepository, times(1)).delete(goal);
    }
}
