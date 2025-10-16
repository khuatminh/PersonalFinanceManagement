package com.finance.controller;

import com.finance.domain.User;
import com.finance.service.ReportService;
import com.finance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showReportPage(Model model, Principal principal) {
        // Generate default monthly report - include current month
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1); // Start of current month

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        ReportService.FinancialSummary summary = reportService.generateFinancialSummary(user, startDateTime, endDateTime);
        ReportService.TrendReport trendReport = reportService.generateTrendReport(user, startDateTime, endDateTime);

        // Prepare data for expense pie chart with null safety
        List<String> expensePieChartLabels = new ArrayList<>();
        List<BigDecimal> expensePieChartData = new ArrayList<>();
        if (summary.getExpenseCategorySummary() != null && !summary.getExpenseCategorySummary().isEmpty()) {
            for (Object[] item : summary.getExpenseCategorySummary()) {
                if (item != null && item.length >= 3 && item[0] != null && item[2] != null) {
                    String categoryName = (String) item[0];
                    BigDecimal amount = (BigDecimal) item[2];
                    if (amount.compareTo(BigDecimal.ZERO) > 0) { // Only include positive amounts
                        expensePieChartLabels.add(categoryName);
                        expensePieChartData.add(amount);
                    }
                }
            }
        }

        // Debug logging
        System.out.println("=== EXPENSE DATA DEBUG ===");
        System.out.println("Raw expense summary size: " + (summary.getExpenseCategorySummary() != null ? summary.getExpenseCategorySummary().size() : 0));
        System.out.println("Filtered expense categories: " + expensePieChartLabels.size());
        for (int i = 0; i < expensePieChartLabels.size(); i++) {
            System.out.println("Expense: " + expensePieChartLabels.get(i) + " = " + expensePieChartData.get(i));
        }

        // Prepare data for income pie chart with null safety
        List<String> incomePieChartLabels = new ArrayList<>();
        List<BigDecimal> incomePieChartData = new ArrayList<>();
        if (summary.getIncomeCategorySummary() != null && !summary.getIncomeCategorySummary().isEmpty()) {
            for (Object[] item : summary.getIncomeCategorySummary()) {
                if (item != null && item.length >= 3 && item[0] != null && item[2] != null) {
                    String categoryName = (String) item[0];
                    BigDecimal amount = (BigDecimal) item[2];
                    if (amount.compareTo(BigDecimal.ZERO) > 0) { // Only include positive amounts
                        incomePieChartLabels.add(categoryName);
                        incomePieChartData.add(amount);
                    }
                }
            }
        }

        // Debug logging
        System.out.println("=== INCOME DATA DEBUG ===");
        System.out.println("Raw income summary size: " + (summary.getIncomeCategorySummary() != null ? summary.getIncomeCategorySummary().size() : 0));
        System.out.println("Filtered income categories: " + incomePieChartLabels.size());
        for (int i = 0; i < incomePieChartLabels.size(); i++) {
            System.out.println("Income: " + incomePieChartLabels.get(i) + " = " + incomePieChartData.get(i));
        }

        model.addAttribute("summary", summary);
        model.addAttribute("trendReport", trendReport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reportGenerated", true);
        model.addAttribute("pageTitle", "Financial Reports - Monthly Overview");
        model.addAttribute("expensePieChartLabels", expensePieChartLabels);
        model.addAttribute("expensePieChartData", expensePieChartData);
        model.addAttribute("incomePieChartLabels", incomePieChartLabels);
        model.addAttribute("incomePieChartData", incomePieChartData);
        model.addAttribute("isDefaultReport", true);

        return "reports/index";
    }

    @PostMapping("/generate")
    public String generateReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        ReportService.FinancialSummary summary = reportService.generateFinancialSummary(user, startDateTime, endDateTime);
        ReportService.TrendReport trendReport = reportService.generateTrendReport(user, startDateTime, endDateTime);

        // Prepare data for expense pie chart with null safety
        List<String> expensePieChartLabels = new ArrayList<>();
        List<BigDecimal> expensePieChartData = new ArrayList<>();
        if (summary.getExpenseCategorySummary() != null && !summary.getExpenseCategorySummary().isEmpty()) {
            for (Object[] item : summary.getExpenseCategorySummary()) {
                if (item != null && item.length >= 3 && item[0] != null && item[2] != null) {
                    String categoryName = (String) item[0];
                    BigDecimal amount = (BigDecimal) item[2];
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        expensePieChartLabels.add(categoryName);
                        expensePieChartData.add(amount);
                    }
                }
            }
        }

        // Prepare data for income pie chart with null safety
        List<String> incomePieChartLabels = new ArrayList<>();
        List<BigDecimal> incomePieChartData = new ArrayList<>();
        if (summary.getIncomeCategorySummary() != null && !summary.getIncomeCategorySummary().isEmpty()) {
            for (Object[] item : summary.getIncomeCategorySummary()) {
                if (item != null && item.length >= 3 && item[0] != null && item[2] != null) {
                    String categoryName = (String) item[0];
                    BigDecimal amount = (BigDecimal) item[2];
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        incomePieChartLabels.add(categoryName);
                        incomePieChartData.add(amount);
                    }
                }
            }
        }

        model.addAttribute("summary", summary);
        model.addAttribute("trendReport", trendReport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("reportGenerated", true);
        model.addAttribute("pageTitle", "Financial Report");
        model.addAttribute("expensePieChartLabels", expensePieChartLabels);
        model.addAttribute("expensePieChartData", expensePieChartData);
        model.addAttribute("incomePieChartLabels", incomePieChartLabels);
        model.addAttribute("incomePieChartData", incomePieChartData);
        model.addAttribute("isDefaultReport", false);

        return "reports/index";
    }

}

