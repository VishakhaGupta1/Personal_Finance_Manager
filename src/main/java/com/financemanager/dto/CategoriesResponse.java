package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for categories list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesResponse {
    private List<CategoryResponse> categories;
}
