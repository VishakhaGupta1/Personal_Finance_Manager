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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceBranchesTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category income;
    private Category expense;
    private Transaction txIncome;
    private Transaction txExpense;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("user").build();
        income = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).build();
        expense = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).build();
        txIncome = Transaction.builder().id(11L).amount(new BigDecimal("100")).date(LocalDate.now().minusDays(2)).description("inc").category(income).user(user).build();
        txExpense = Transaction.builder().id(12L).amount(new BigDecimal("50")).date(LocalDate.now().minusDays(1)).description("exp").category(expense).user(user).build();
    }

    @Test
    void getTransactions_categoryNameOnly_returnsByCategory() {
        when(categoryService.getCategoryByName("Food", user)).thenReturn(expense);
        when(transactionRepository.findByUserAndCategory(user, 2L)).thenReturn(List.of(txExpense));

        var resp = transactionService.getTransactions(user, null, null, "Food", null, null);
        assertEquals(1, resp.getTransactions().size());
        assertEquals("Food", resp.getTransactions().get(0).getCategory());
    }

    @Test
    void getTransactions_noFilter_returnsOrderedDesc() {
        when(transactionRepository.findByUserOrderByDateDesc(user)).thenReturn(List.of(txExpense, txIncome));

        var resp = transactionService.getTransactions(user, null, null, null, null, null);
        assertEquals(2, resp.getTransactions().size());
        assertEquals("Food", resp.getTransactions().get(0).getCategory());
        assertEquals("Salary", resp.getTransactions().get(1).getCategory());
    }
}
