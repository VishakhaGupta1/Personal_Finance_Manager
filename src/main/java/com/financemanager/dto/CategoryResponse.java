package com.financemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for category response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String name;
    private String type;

    @JsonProperty("isCustom")
    private Boolean isCustom;

    // ✅ Alias for E2E test compatibility
    @JsonProperty("custom")
    public Boolean getCustom() {
        return isCustom;
    }
}