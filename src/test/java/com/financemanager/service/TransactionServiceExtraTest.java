package com.financemanager.service;

import com.financemanager.dto.UpdateTransactionRequest;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceExtraTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category income;
    private Category expense;
    private Transaction tx;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("u").build();
        income = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).build();
        expense = Category.builder().id(2L).name("Food").type(CategoryType.EXPENSE).build();
        tx = Transaction.builder().id(10L).amount(new BigDecimal("10")).date(LocalDate.now().minusDays(1)).description("d").category(expense).user(user).build();
    }

    @Test
    void getTransactions_invalidType_throws() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactions(user, null, null, null, null, "INVALID"));
    }

    @Test
    void getTransactions_categoryIdWithMismatchedType_returnsEmpty() {
        given(categoryService.getCategoryById(1L, user)).willReturn(income);
        var resp = transactionService.getTransactions(user, "2024-01-01", "2024-01-31", null, 1L, "EXPENSE");
        assertTrue(resp.getTransactions().isEmpty());
    }

    @Test
    void getTransactions_typeOnly_callsTypeRepo() {
        given(transactionRepository.findByUserAndTypeOrderByDateDesc(user, CategoryType.EXPENSE)).willReturn(List.of(tx));
        var resp = transactionService.getTransactions(user, null, null, null, null, "EXPENSE");
        assertEquals(1, resp.getTransactions().size());
    }

    @Test
    void updateTransaction_updatesFields() {
        given(transactionRepository.findById(10L)).willReturn(java.util.Optional.of(tx));
        given(transactionRepository.save(any(Transaction.class))).willAnswer(i -> i.getArgument(0));
        given(categoryService.getCategoryByName("Salary", user)).willReturn(income);

        UpdateTransactionRequest req = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("25"))
                .description("new")
                .category("Salary")
                .build();

        var resp = transactionService.updateTransaction(10L, req, user);
        assertEquals(new BigDecimal("25"), resp.getAmount());
        assertEquals("Salary", resp.getCategory());
        assertEquals("new", resp.getDescription());
    }
}
