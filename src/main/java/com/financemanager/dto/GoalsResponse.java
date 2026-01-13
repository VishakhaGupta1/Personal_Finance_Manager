package com.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for goals list response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalsResponse {
    private List<GoalResponse> goals;
}
