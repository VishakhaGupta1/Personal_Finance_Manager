package com.financemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financemanager.dto.CreateGoalRequest;
import com.financemanager.dto.GoalResponse;
import com.financemanager.dto.GoalsResponse;
import com.financemanager.dto.UpdateGoalRequest;
import com.financemanager.entity.User;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.SavingsGoalService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SavingsGoalController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavingsGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SavingsGoalService savingsGoalService;

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
    void createGoal_returnsCreated() throws Exception {
        String targetDate = LocalDate.now().plusMonths(6).toString();
        String startDate = LocalDate.now().toString();
        CreateGoalRequest req = CreateGoalRequest.builder()
                .goalName("Emergency Fund")
                .targetAmount(new BigDecimal("5000"))
                .targetDate(targetDate)
                .startDate(startDate)
                .build();

        GoalResponse resp = GoalResponse.builder()
                .id(10L)
                .goalName(req.getGoalName())
                .targetAmount(req.getTargetAmount())
                .targetDate(LocalDate.parse(targetDate))
                .startDate(LocalDate.parse(startDate))
                .currentProgress(new BigDecimal("0"))
                .progressPercentage(0.0)
                .remainingAmount(req.getTargetAmount())
                .build();

        given(savingsGoalService.createGoal(any(CreateGoalRequest.class), eq(user))).willReturn(resp);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    void createGoal_invalidDatePattern_returnsBadRequest() throws Exception {
        CreateGoalRequest req = CreateGoalRequest.builder()
                .goalName("G")
                .targetAmount(new BigDecimal("1000"))
                .targetDate("2026/01/01") // invalid format
                .startDate("2025/01/01")
                .build();

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllGoals_returnsList() throws Exception {
        GoalResponse g1 = GoalResponse.builder().id(1L).goalName("G1").build();
        GoalResponse g2 = GoalResponse.builder().id(2L).goalName("G2").build();
        given(savingsGoalService.getAllGoals(user)).willReturn(GoalsResponse.builder().goals(List.of(g1, g2)).build());

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.length()").value(2));
    }

    @Test
    void getGoal_returnsOne() throws Exception {
        GoalResponse g1 = GoalResponse.builder().id(1L).goalName("G1").build();
        given(savingsGoalService.getGoal(1L, user)).willReturn(g1);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateGoal_returnsUpdated() throws Exception {
        UpdateGoalRequest req = UpdateGoalRequest.builder()
                .targetAmount(new BigDecimal("6000"))
                .targetDate(LocalDate.now().plusMonths(7).toString())
                .build();

        GoalResponse updated = GoalResponse.builder().id(1L).goalName("G1").targetAmount(new BigDecimal("6000")).build();
        given(savingsGoalService.updateGoal(eq(1L), any(UpdateGoalRequest.class), eq(user))).willReturn(updated);

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(6000));
    }

    @Test
    void deleteGoal_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/goals/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }
}
