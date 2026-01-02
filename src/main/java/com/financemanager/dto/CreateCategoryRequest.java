package com.financemanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a custom category request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Category type is required")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Category type must be INCOME or EXPENSE")
    private String type;
}
