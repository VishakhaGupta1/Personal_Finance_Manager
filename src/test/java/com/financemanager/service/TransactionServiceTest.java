package com.financemanager.service;

import com.financemanager.dto.CreateTransactionRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;
    private Transaction transaction;
    private CreateTransactionRequest createRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .fullName("John Doe")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Salary")
                .type(CategoryType.INCOME)
                .isCustom(false)
                .user(null)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(5000))
                .date(LocalDate.now().minusDays(1))
                .description("January Salary")
                .category(category)
                .user(user)
                .build();

        createRequest = CreateTransactionRequest.builder()
                .amount(BigDecimal.valueOf(5000))
                .date(LocalDate.now().minusDays(1).toString())
                .category("Salary")
                .description("January Salary")
                .build();
    }

    @Test
    void testCreateTransactionSuccess() {
        when(categoryService.getCategoryByName("Salary", user)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        var response = transactionService.createTransaction(createRequest, user);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(BigDecimal.valueOf(5000), response.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionFutureDate() {
        createRequest.setDate(LocalDate.now().plusDays(1).toString());

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(createRequest, user));
    }

    @Test
    void testDeleteTransactionForbidden() {
        User otherUser = User.builder()
                .id(2L)
                .username("other@example.com")
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(ForbiddenException.class, () -> transactionService.deleteTransaction(1L, otherUser));
    }

    @Test
    void testDeleteTransactionNotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(1L, user));
    }
}
