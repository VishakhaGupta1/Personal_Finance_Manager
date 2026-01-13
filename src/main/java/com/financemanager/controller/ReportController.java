package com.financemanager.controller;

import com.financemanager.dto.MonthlyReportResponse;
import com.financemanager.dto.YearlyReportResponse;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for financial reports endpoints.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReportController {

    private final ReportService reportService;
    private final AuthenticationService authenticationService;

    /**
     * Get monthly report.
     *
     * @param year Report year
     * @param month Report month (1-12)
     * @return MonthlyReportResponse with report data
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @PathVariable int year,
            @PathVariable int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year <= 0) {
            throw new IllegalArgumentException("Year must be a positive integer");
        }
        var user = authenticationService.getCurrentUser();
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Get yearly report.
     *
     * @param year Report year
     * @return YearlyReportResponse with report data
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(@PathVariable int year) {
        if (year <= 0) {
            throw new IllegalArgumentException("Year must be a positive integer");
        }
        var user = authenticationService.getCurrentUser();
        YearlyReportResponse response = reportService.getYearlyReport(year, user);
        return ResponseEntity.ok(response);
    }
}
