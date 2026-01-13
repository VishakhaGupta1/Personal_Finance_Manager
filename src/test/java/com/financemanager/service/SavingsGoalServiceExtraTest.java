package com.financemanager.service;

import com.financemanager.dto.UpdateGoalRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.Transaction;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceExtraTest {

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
                .targetDate(LocalDate.now().plusMonths(1))
                .startDate(LocalDate.now().minusMonths(1))
                .user(user)
                .build();
    }

    @Test
    void updateGoal_invalidNewTargetDate_notFuture() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));
        UpdateGoalRequest req = UpdateGoalRequest.builder().targetDate(LocalDate.now().toString()).build();
        assertThrows(IllegalArgumentException.class, () -> savingsGoalService.updateGoal(1L, req, user));
    }

    @Test
    void updateGoal_invalidNewTargetDate_beforeStart() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));
        UpdateGoalRequest req = UpdateGoalRequest.builder().targetDate(LocalDate.now().minusMonths(2).toString()).build();
        assertThrows(IllegalArgumentException.class, () -> savingsGoalService.updateGoal(1L, req, user));
    }

    @Test
    void toResponse_capsProgressAt100_andNonNegativeRemaining() {
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(goal));
        Category income = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).build();
        Category expense = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).build();
        List<Transaction> txs = List.of(
                Transaction.builder().amount(new BigDecimal("1500")).category(income).date(LocalDate.now()).user(user).build(),
                Transaction.builder().amount(new BigDecimal("100")).category(expense).date(LocalDate.now()).user(user).build()
        );
        when(transactionRepository.findByUserAndDateRange(eq(user), any(), any())).thenReturn(txs);

        var resp = savingsGoalService.getGoal(1L, user);
        assertEquals(100.0, resp.getProgressPercentage());
        assertEquals(BigDecimal.ZERO, resp.getRemainingAmount());
    }
}
