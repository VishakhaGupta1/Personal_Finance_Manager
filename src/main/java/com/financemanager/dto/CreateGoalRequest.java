package com.financemanager.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for creating a savings goal request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoalRequest {
    @NotBlank(message = "Goal name is required")
    @Size(min = 2, max = 100, message = "Goal name must be between 2 and 100 characters")
    private String goalName;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    @NotBlank(message = "Target date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Target date must be in YYYY-MM-DD format")
    private String targetDate;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Start date must be in YYYY-MM-DD format")
    private String startDate;
}
