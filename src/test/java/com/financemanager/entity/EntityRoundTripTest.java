package com.financemanager.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EntityRoundTripTest {

    @Test
    void category_builder_and_getters() {
        User user = User.builder().id(1L).username("u").password("p").fullName("User").phoneNumber("+1234567890").build();
        Category cat = Category.builder()
                .id(2L)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .isCustom(true)
                .user(user)
                .build();
        assertEquals("Food", cat.getName());
        assertEquals(CategoryType.EXPENSE, cat.getType());
        assertTrue(cat.getIsCustom());
        assertEquals(1L, cat.getUser().getId());
    }

    @Test
    void transaction_builder_and_lifecycle() {
        User user = User.builder().id(1L).username("u").password("p").fullName("User").phoneNumber("+1234567890").build();
        Category income = Category.builder().id(3L).name("Salary").type(CategoryType.INCOME).isCustom(false).user(null).build();
        Transaction tx = Transaction.builder()
                .id(10L)
                .amount(new BigDecimal("100.00"))
                .date(LocalDate.now())
                .description("desc")
                .category(income)
                .user(user)
                .build();
        // simulate lifecycle callbacks
        tx.onCreate();
        assertNotNull(tx.getCreatedAt());
        assertNotNull(tx.getUpdatedAt());
        tx.onUpdate();
        assertNotNull(tx.getUpdatedAt());
    }

    @Test
    void savingsGoal_builder_and_lifecycle() {
        User user = User.builder().id(1L).username("u").password("p").fullName("User").phoneNumber("+1234567890").build();
        SavingsGoal goal = SavingsGoal.builder()
                .id(5L)
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000"))
                .targetDate(LocalDate.now().plusMonths(6))
                .startDate(LocalDate.now())
                .user(user)
                .build();
        goal.onCreate();
        assertNotNull(goal.getCreatedAt());
        assertNotNull(goal.getUpdatedAt());
        goal.onUpdate();
        assertNotNull(goal.getUpdatedAt());
    }

    @Test
    void user_builder_and_onCreate() {
        User user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("pass")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();
        user.onCreate();
        assertEquals("test@example.com", user.getUsername());
        assertNotNull(user.getCreatedAt());
    }
}
