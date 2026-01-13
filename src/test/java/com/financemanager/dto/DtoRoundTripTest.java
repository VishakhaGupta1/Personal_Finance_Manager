package com.financemanager.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoRoundTripTest {

    @Test
    void minimalRoundTrips_work() {
        // Transaction DTOs
        CreateTransactionRequest ctr = CreateTransactionRequest.builder()
                .amount(new BigDecimal("10"))
                .date("2024-01-01")
                .category("Food")
                .description("D")
                .build();
        assertEquals("Food", ctr.getCategory());
        TransactionResponse tr = TransactionResponse.builder()
                .id(1L)
                .amount(new BigDecimal("10"))
                .date(LocalDate.of(2024,1,1))
                .category("Food")
                .description("D")
                .type("EXPENSE")
                .build();
        TransactionsResponse trs = TransactionsResponse.builder().transactions(List.of(tr)).build();
        assertEquals(1, trs.getTransactions().size());

        // Category DTOs
        CreateCategoryRequest ccr = CreateCategoryRequest.builder().name("Gifts").type("EXPENSE").build();
        CategoryResponse cr = CategoryResponse.builder().name("Salary").type("INCOME").isCustom(false).build();
        CategoriesResponse all = CategoriesResponse.builder().categories(List.of(cr)).build();
        assertEquals("Gifts", ccr.getName());
        assertFalse(all.getCategories().get(0).getIsCustom());

        // Goal DTOs
        CreateGoalRequest cgr = CreateGoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000"))
                .targetDate("2025-12-31")
                .startDate("2025-01-01")
                .build();
        UpdateGoalRequest ugr = UpdateGoalRequest.builder().targetAmount(new BigDecimal("6000")).targetDate("2026-01-01").build();
        GoalResponse gr = GoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000"))
                .targetDate(LocalDate.of(2025,12,31))
                .startDate(LocalDate.of(2025,1,1))
                .currentProgress(new BigDecimal("2500"))
                .progressPercentage(50.0)
                .remainingAmount(new BigDecimal("2500"))
                .build();
        GoalsResponse grs = GoalsResponse.builder().goals(List.of(gr)).build();
        assertEquals(1, grs.getGoals().size());

        // Reports DTOs
        MonthlyReportResponse mrr = MonthlyReportResponse.builder()
                .month(1)
                .year(2025)
                .totalIncome(Map.of("Salary", new BigDecimal("1000")))
                .totalExpenses(Map.of("Food", new BigDecimal("200")))
                .netSavings(new BigDecimal("800"))
                .build();
        YearlyReportResponse yrr = YearlyReportResponse.builder()
                .year(2025)
                .totalIncome(Map.of("Salary", new BigDecimal("12000")))
                .totalExpenses(Map.of("Rent", new BigDecimal("6000")))
                .netSavings(new BigDecimal("6000"))
                .build();
        assertEquals(2025, yrr.getYear());

        // Auth & Message
        AuthResponse ar = AuthResponse.builder().message("ok").userId(99L).build();
        MessageResponse mr = MessageResponse.builder().message("hi").build();
        LoginRequest lr = LoginRequest.builder().username("u").password("p").build();
        RegisterRequest rr = RegisterRequest.builder().username("u@e.com").password("secret12").fullName("User Example").phoneNumber("+1234567890").build();
        assertEquals("ok", ar.getMessage());
        assertEquals("hi", mr.getMessage());
        assertEquals("u", lr.getUsername());
        assertEquals("User Example", rr.getFullName());
    }
}
