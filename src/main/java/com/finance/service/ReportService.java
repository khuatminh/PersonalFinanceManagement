package com.finance.service;

import com.finance.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private TransactionService transactionService;


    public FinancialSummary generateFinancialSummary(User user, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalIncome = transactionService.getTotalIncomeByUserAndDateRange(user, startDate, endDate);
        BigDecimal totalExpenses = transactionService.getTotalExpensesByUserAndDateRange(user, startDate, endDate);
        List<Object[]> expenseCategorySummary = transactionService.getExpenseCategorySummaryForDateRange(user, startDate, endDate);
        List<Object[]> incomeCategorySummary = transactionService.getIncomeCategorySummaryForDateRange(user, startDate, endDate);

        return new FinancialSummary(totalIncome, totalExpenses, expenseCategorySummary, incomeCategorySummary);
    }


    public TrendReport generateTrendReport(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> trendData = transactionService.getTransactionTrendByDate(user, startDate, endDate);

        List<String> labels = new ArrayList<>();
        List<BigDecimal> incomeData = new ArrayList<>();
        List<BigDecimal> expenseData = new ArrayList<>();

        for (Object[] row : trendData) {
            Date date = (Date) row[0];
            labels.add(date.toLocalDate().toString());
            incomeData.add((BigDecimal) row[1]);
            expenseData.add((BigDecimal) row[2]);
        }

        return new TrendReport(labels, incomeData, expenseData);
    }

    public static class TrendReport {
        private final List<String> labels;
        private final List<BigDecimal> incomeData;
        private final List<BigDecimal> expenseData;

        public TrendReport(List<String> labels, List<BigDecimal> incomeData, List<BigDecimal> expenseData) {
            this.labels = labels;
            this.incomeData = incomeData;
            this.expenseData = expenseData;
        }

        public List<String> getLabels() { return labels; }
        public List<BigDecimal> getIncomeData() { return incomeData; }
        public List<BigDecimal> getExpenseData() { return expenseData; }
    }

    public static class FinancialSummary {
        private final BigDecimal totalIncome;
        private final BigDecimal totalExpenses;
        private final List<Object[]> expenseCategorySummary;
        private final List<Object[]> incomeCategorySummary;

        public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses, List<Object[]> expenseCategorySummary, List<Object[]> incomeCategorySummary) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.expenseCategorySummary = expenseCategorySummary;
            this.incomeCategorySummary = incomeCategorySummary;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome;
        }

        public BigDecimal getTotalExpenses() {
            return totalExpenses;
        }

        public List<Object[]> getExpenseCategorySummary() {
            return expenseCategorySummary;
        }

        public List<Object[]> getIncomeCategorySummary() {
            return incomeCategorySummary;
        }

        public BigDecimal getNetSavings() {
            return totalIncome.subtract(totalExpenses);
        }
    }
}

