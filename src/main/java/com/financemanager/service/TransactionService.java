package com.financemanager.service;

import com.financemanager.dto.CreateTransactionRequest;
import com.financemanager.dto.TransactionResponse;
import com.financemanager.dto.TransactionsResponse;
import com.financemanager.dto.UpdateTransactionRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for transaction management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Create a new transaction.
     *
     * @param request Create transaction request
     * @param user Current user
     * @return TransactionResponse with created transaction
     * @throws IllegalArgumentException if date is in the future
     */
    public TransactionResponse createTransaction(CreateTransactionRequest request, User user) {
        LocalDate transactionDate = LocalDate.parse(request.getDate(), DATE_FORMATTER);

        if (transactionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Transaction date cannot be in the future");
        }

        Category category = categoryService.getCategoryByName(request.getCategory(), user);

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(transactionDate)
                .description(request.getDescription())
                .category(category)
                .user(user)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return toResponse(savedTransaction);
    }

    /**
     * Get all transactions for a user.
     *
     * @param user Current user
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param categoryName Optional category name filter
     * @return TransactionsResponse with list of transactions
     */
    public TransactionsResponse getTransactions(User user, String startDate, String endDate, String categoryName, Long categoryId, String typeName) {
        List<Transaction> transactions;
        // Parse optional type filter
        com.financemanager.entity.CategoryType typeFilter = null;
        if (typeName != null && !typeName.isBlank()) {
            try {
                typeFilter = com.financemanager.entity.CategoryType.valueOf(typeName.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid type. Allowed values: INCOME, EXPENSE");
            }
        }

        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);

            if (categoryId != null) {
                // If categoryId provided, optionally verify type filter matches
                Category category = categoryService.getCategoryById(categoryId, user);
                if (typeFilter != null && category.getType() != typeFilter) {
                    transactions = List.of();
                } else {
                    transactions = transactionRepository.findByUserDateRangeAndCategory(user, start, end, categoryId);
                }
            } else if (categoryName != null && !categoryName.isBlank()) {
                Category category = categoryService.getCategoryByName(categoryName, user);
                if (typeFilter != null && category.getType() != typeFilter) {
                    transactions = List.of();
                } else {
                    transactions = transactionRepository.findByUserDateRangeAndCategory(user, start, end, category.getId());
                }
            } else if (typeFilter != null) {
                transactions = transactionRepository.findByUserAndDateRangeAndType(user, start, end, typeFilter);
            } else {
                transactions = transactionRepository.findByUserAndDateRange(user, start, end);
            }
        } else if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId, user);
            if (typeFilter != null && category.getType() != typeFilter) {
                transactions = List.of();
            } else {
                transactions = transactionRepository.findByUserAndCategory(user, categoryId);
            }
        } else if (categoryName != null && !categoryName.isBlank()) {
            Category category = categoryService.getCategoryByName(categoryName, user);
            if (typeFilter != null && category.getType() != typeFilter) {
                transactions = List.of();
            } else {
                transactions = transactionRepository.findByUserAndCategory(user, category.getId());
            }
        } else if (typeFilter != null) {
            transactions = transactionRepository.findByUserAndTypeOrderByDateDesc(user, typeFilter);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDesc(user);
        }

        List<TransactionResponse> responses = transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return TransactionsResponse.builder()
                .transactions(responses)
                .build();
    }

    /**
     * Get a transaction by ID.
     *
     * @param id Transaction ID
     * @param user Current user
     * @return Transaction if found and belongs to user
     * @throws ResourceNotFoundException if transaction not found
     * @throws ForbiddenException if transaction belongs to another user
     */
    public Transaction getTransactionById(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to access this transaction");
        }

        return transaction;
    }

    /**
     * Update a transaction.
     *
     * @param id Transaction ID
     * @param request Update transaction request
     * @param user Current user
     * @return TransactionResponse with updated transaction
     * @throws ResourceNotFoundException if transaction not found
     * @throws ForbiddenException if transaction belongs to another user
     */
    public TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request, User user) {
        Transaction transaction = getTransactionById(id, user);

        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            Category newCategory = categoryService.getCategoryByName(request.getCategory(), user);
            transaction.setCategory(newCategory);
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return toResponse(updatedTransaction);
    }

    /**
     * Delete a transaction.
     *
     * @param id Transaction ID
     * @param user Current user
     * @throws ResourceNotFoundException if transaction not found
     * @throws ForbiddenException if transaction belongs to another user
     */
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = getTransactionById(id, user);
        transactionRepository.delete(transaction);
    }

    /**
     * Convert Transaction entity to TransactionResponse.
     *
     * @param transaction Transaction entity
     * @return TransactionResponse
     */
    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .category(transaction.getCategory().getName())
                .description(transaction.getDescription())
                .type(transaction.getCategory().getType().toString())
                .build();
    }
}
