package com.financemanager.controller;

import com.financemanager.dto.CreateTransactionRequest;
import com.financemanager.dto.MessageResponse;
import com.financemanager.dto.TransactionResponse;
import com.financemanager.dto.TransactionsResponse;
import com.financemanager.dto.UpdateTransactionRequest;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for transaction management endpoints.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;

    /**
     * Create a new transaction.
     *
     * @param request Create transaction request
     * @return TransactionResponse with created transaction
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        var user = authenticationService.getCurrentUser();
        TransactionResponse response = transactionService.createTransaction(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get transactions with optional filters.
     *
     * @param startDate Optional start date (YYYY-MM-DD)
     * @param endDate Optional end date (YYYY-MM-DD)
     * @param categoryId Optional category ID
     * @return TransactionsResponse with list of transactions
     */
    @GetMapping
    public ResponseEntity<TransactionsResponse> getTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String type) {
        var user = authenticationService.getCurrentUser();
        TransactionsResponse response = transactionService.getTransactions(user, startDate, endDate, category, categoryId, type);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a transaction.
     *
     * @param id Transaction ID
     * @param request Update transaction request
     * @return TransactionResponse with updated transaction
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionRequest request) {
        var user = authenticationService.getCurrentUser();
        TransactionResponse response = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a transaction.
     *
     * @param id Transaction ID
     * @return MessageResponse with success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTransaction(@PathVariable Long id) {
        var user = authenticationService.getCurrentUser();
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Transaction deleted successfully")
                .build());
    }
}
