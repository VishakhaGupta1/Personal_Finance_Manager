package com.financemanager.service;

import com.financemanager.dto.MonthlyReportResponse;
import com.financemanager.dto.YearlyReportResponse;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import com.financemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating financial reports.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final TransactionRepository transactionRepository;

    /**
     * Generate a monthly report for a specific month and year.
     *
     * @param year Year of the report
     * @param month Month of the report (1-12)
     * @param user Current user
     * @return MonthlyReportResponse with income, expenses, and net savings
     */
    public MonthlyReportResponse getMonthlyReport(int year, int month, User user) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Transaction> transactions = transactionRepository.findByUserAndDateRange(user, startDate, endDate);

        Map<String, BigDecimal> totalIncome = new LinkedHashMap<>();
        Map<String, BigDecimal> totalExpenses = new LinkedHashMap<>();
        BigDecimal netSavings = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            if (transaction.getCategory().getType() == CategoryType.INCOME) {
                totalIncome.merge(categoryName, amount, BigDecimal::add);
                netSavings = netSavings.add(amount);
            } else {
                totalExpenses.merge(categoryName, amount, BigDecimal::add);
                netSavings = netSavings.subtract(amount);
            }
        }

        return MonthlyReportResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .build();
    }

    /**
     * Generate a yearly report for a specific year.
     *
     * @param year Year of the report
     * @param user Current user
     * @return YearlyReportResponse with income, expenses, and net savings for the entire year
     */
    public YearlyReportResponse getYearlyReport(int year, User user) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByUserAndDateRange(user, startDate, endDate);

        Map<String, BigDecimal> totalIncome = new LinkedHashMap<>();
        Map<String, BigDecimal> totalExpenses = new LinkedHashMap<>();
        BigDecimal netSavings = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            if (transaction.getCategory().getType() == CategoryType.INCOME) {
                totalIncome.merge(categoryName, amount, BigDecimal::add);
                netSavings = netSavings.add(amount);
            } else {
                totalExpenses.merge(categoryName, amount, BigDecimal::add);
                netSavings = netSavings.subtract(amount);
            }
        }

        return YearlyReportResponse.builder()
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .build();
    }
}
