package com.financemanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for updating a savings goal request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGoalRequest {
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Target date must be in YYYY-MM-DD format")
    private String targetDate;
}
