package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for transactions list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionsResponse {
    private List<TransactionResponse> transactions;
}
