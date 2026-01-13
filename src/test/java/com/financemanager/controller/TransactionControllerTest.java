package com.financemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financemanager.dto.CreateTransactionRequest;
import com.financemanager.dto.TransactionResponse;
import com.financemanager.dto.TransactionsResponse;
import com.financemanager.dto.UpdateTransactionRequest;
import com.financemanager.entity.User;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("password")
                .fullName("Test User")
                .phoneNumber("+10000000000")
                .build();
        given(authenticationService.getCurrentUser()).willReturn(user);
    }

    @Test
    void createTransaction_returnsCreated() throws Exception {
        CreateTransactionRequest req = CreateTransactionRequest.builder()
                .amount(new BigDecimal("100.50"))
                .date(LocalDate.now().toString())
                .category("Food")
                .description("Lunch")
                .build();

        TransactionResponse resp = TransactionResponse.builder()
                .id(100L)
                .amount(new BigDecimal("100.50"))
                .date(LocalDate.parse(req.getDate()))
                .category("Food")
                .description("Lunch")
                .type("EXPENSE")
                .build();

        given(transactionService.createTransaction(any(CreateTransactionRequest.class), eq(user))).willReturn(resp);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    void createTransaction_validationError_returnsBadRequest() throws Exception {
        // missing amount triggers @NotNull validation error
        String body = "{\n" +
                "  \"date\": \"2024-01-10\",\n" +
                "  \"category\": \"Food\",\n" +
                "  \"description\": \"Lunch\"\n" +
                "}";

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactions_withFilters_returnsList() throws Exception {
        TransactionResponse t1 = TransactionResponse.builder().id(1L).category("Salary").type("INCOME").build();
        TransactionResponse t2 = TransactionResponse.builder().id(2L).category("Food").type("EXPENSE").build();
        given(transactionService.getTransactions(eq(user), anyString(), anyString(), any(), any(), any()))
                .willReturn(TransactionsResponse.builder().transactions(List.of(t1, t2)).build());

        mockMvc.perform(get("/api/transactions")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("categoryId", "1")
                        .param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(2));
    }

    @Test
    void updateTransaction_returnsOk() throws Exception {
        UpdateTransactionRequest req = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("120.00"))
                .description("Lunch + dessert")
                .build();
        TransactionResponse updated = TransactionResponse.builder().id(2L).amount(new BigDecimal("120.00")).build();
        given(transactionService.updateTransaction(eq(2L), any(UpdateTransactionRequest.class), eq(user))).willReturn(updated);

        mockMvc.perform(put("/api/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(120.00));
    }

    @Test
    void deleteTransaction_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/transactions/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }
}
