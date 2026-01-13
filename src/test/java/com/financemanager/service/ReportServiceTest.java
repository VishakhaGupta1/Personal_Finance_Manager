package com.financemanager.service;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ReportService.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        Category incomeCategory = Category.builder()
                .id(1L)
                .name("Salary")
                .type(CategoryType.INCOME)
                .user(null)
                .build();

        Category expenseCategory = Category.builder()
                .id(2L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .user(null)
                .build();

        transactions = new ArrayList<>();
        transactions.add(Transaction.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(5000))
                .date(LocalDate.of(2024, 1, 15))
                .category(incomeCategory)
                .user(user)
                .build());

        transactions.add(Transaction.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(500))
                .date(LocalDate.of(2024, 1, 20))
                .category(expenseCategory)
                .user(user)
                .build());
    }

    @Test
    void testGetMonthlyReport() {
        when(transactionRepository.findByUserAndDateRange(eq(user), any(), any()))
                .thenReturn(transactions);

        var response = reportService.getMonthlyReport(2024, 1, user);

        assertNotNull(response);
        assertEquals(1, response.getMonth());
        assertEquals(2024, response.getYear());
        assertNotNull(response.getTotalIncome());
        assertNotNull(response.getTotalExpenses());
        assertTrue(response.getTotalIncome().containsKey("Salary"));
        assertTrue(response.getTotalExpenses().containsKey("Food"));
    }

    @Test
    void testGetYearlyReport() {
        when(transactionRepository.findByUserAndDateRange(eq(user), any(), any()))
                .thenReturn(transactions);

        var response = reportService.getYearlyReport(2024, user);

        assertNotNull(response);
        assertEquals(2024, response.getYear());
        assertNotNull(response.getTotalIncome());
        assertNotNull(response.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(4500), response.getNetSavings());
    }
}
