package com.financemanager.controller;

import com.financemanager.dto.MonthlyReportResponse;
import com.financemanager.dto.YearlyReportResponse;
import com.financemanager.entity.User;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("test@example.com").password("p").fullName("t").phoneNumber("+1").build();
        given(authenticationService.getCurrentUser()).willReturn(user);
    }

    @Test
    void getMonthlyReport_returnsOk() throws Exception {
        MonthlyReportResponse resp = MonthlyReportResponse.builder()
                .year(2024)
                .month(1)
                .totalIncome(Map.of("Salary", new BigDecimal("3000.00")))
                .totalExpenses(Map.of("Food", new BigDecimal("400.00")))
                .netSavings(new BigDecimal("2600.00"))
                .build();
        given(reportService.getMonthlyReport(2024, 1, user)).willReturn(resp);

        mockMvc.perform(get("/api/reports/monthly/2024/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.month").value(1));
    }

    @Test
    void getMonthlyReport_invalidMonth_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/monthly/2024/13"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getYearlyReport_invalidYear_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/yearly/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getYearlyReport_returnsOk() throws Exception {
        YearlyReportResponse resp = YearlyReportResponse.builder()
                .year(2024)
                .totalIncome(Map.of("Salary", new BigDecimal("36000.00")))
                .totalExpenses(Map.of("Food", new BigDecimal("4800.00")))
                .netSavings(new BigDecimal("31200.00"))
                .build();
        given(reportService.getYearlyReport(2024, user)).willReturn(resp);

        mockMvc.perform(get("/api/reports/yearly/2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024));
    }
}
