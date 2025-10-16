package com.finance.utils;

import com.finance.domain.Transaction;
import com.finance.domain.Budget;
import com.finance.domain.Goal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Financial Calculation Utilities
 * 
 * This utility class provides common financial calculations and operations
 * used throughout the Personal Finance Manager application. All methods are
 * static and thread-safe for use across the application.
 * 
 * Key Features:
 * - Transaction amount calculations and aggregations
 * - Budget progress and variance calculations
 * - Goal progress tracking and projections
 * - Balance calculations with proper decimal handling
 * - Percentage calculations for financial metrics
 * 
 * Precision Handling:
 * - All monetary calculations use BigDecimal for precision
 * - Default rounding mode is HALF_UP for financial accuracy
 * - Scale is set to 2 decimal places for currency representation
 * 
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */
public final class FinancialCalculationUtils {

    // ================================
    // CONSTANTS
    // ================================

    /** Default scale for monetary calculations (2 decimal places) */
    public static final int MONETARY_SCALE = 2;
    
    /** Default rounding mode for financial calculations */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;
    
    /** Scale for percentage calculations (4 decimal places for precision) */
    public static final int PERCENTAGE_SCALE = 4;
    
    /** Zero amount constant for comparisons */
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    
    /** One hundred constant for percentage calculations */
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    // ================================
    // CONSTRUCTOR
    // ================================

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private FinancialCalculationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ================================
    // TRANSACTION CALCULATIONS
    // ================================

    /**
     * Calculates the total income from a list of transactions.
     * 
     * @param transactions List of transactions to analyze
     * @return Total income amount with proper scale and rounding
     * @throws IllegalArgumentException if transactions list is null
     */
    public static BigDecimal calculateTotalIncome(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        return transactions.stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(ZERO, BigDecimal::add)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculates the total expenses from a list of transactions.
     * 
     * @param transactions List of transactions to analyze
     * @return Total expense amount with proper scale and rounding
     * @throws IllegalArgumentException if transactions list is null
     */
    public static BigDecimal calculateTotalExpenses(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        return transactions.stream()
                .filter(Transaction::isExpense)
                .map(Transaction::getAmount)
                .reduce(ZERO, BigDecimal::add)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculates the net balance (income - expenses) from a list of transactions.
     * 
     * @param transactions List of transactions to analyze
     * @return Net balance with proper scale and rounding
     * @throws IllegalArgumentException if transactions list is null
     */
    public static BigDecimal calculateNetBalance(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        BigDecimal totalIncome = calculateTotalIncome(transactions);
        BigDecimal totalExpenses = calculateTotalExpenses(transactions);
        
        return totalIncome.subtract(totalExpenses)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Groups transactions by category and calculates totals for each category.
     * 
     * @param transactions List of transactions to group
     * @return Map of category names to total amounts
     * @throws IllegalArgumentException if transactions list is null
     */
    public static Map<String, BigDecimal> calculateCategoryTotals(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        return transactions.stream()
                .collect(Collectors.groupingBy(
                    t -> t.getCategory().getName(),
                    Collectors.reducing(ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // ================================
    // BUDGET CALCULATIONS
    // ================================

    /**
     * Calculates the budget utilization percentage.
     * 
     * @param spent Amount already spent
     * @param budgetAmount Total budget amount
     * @return Utilization percentage (0-100+)
     * @throws IllegalArgumentException if any parameter is null or budget amount is zero
     */
    public static BigDecimal calculateBudgetUtilization(BigDecimal spent, BigDecimal budgetAmount) {
        if (spent == null) {
            throw new IllegalArgumentException("Spent amount cannot be null");
        }
        if (budgetAmount == null) {
            throw new IllegalArgumentException("Budget amount cannot be null");
        }
        if (budgetAmount.compareTo(ZERO) == 0) {
            throw new IllegalArgumentException("Budget amount cannot be zero");
        }
        
        return spent.divide(budgetAmount, PERCENTAGE_SCALE, DEFAULT_ROUNDING)
                .multiply(ONE_HUNDRED)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculates the remaining budget amount.
     * 
     * @param budgetAmount Total budget amount
     * @param spent Amount already spent
     * @return Remaining budget amount (can be negative if over budget)
     * @throws IllegalArgumentException if any parameter is null
     */
    public static BigDecimal calculateRemainingBudget(BigDecimal budgetAmount, BigDecimal spent) {
        if (budgetAmount == null) {
            throw new IllegalArgumentException("Budget amount cannot be null");
        }
        if (spent == null) {
            throw new IllegalArgumentException("Spent amount cannot be null");
        }
        
        return budgetAmount.subtract(spent)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Determines if a budget is over the limit.
     * 
     * @param spent Amount already spent
     * @param budgetAmount Total budget amount
     * @return true if spent amount exceeds budget, false otherwise
     * @throws IllegalArgumentException if any parameter is null
     */
    public static boolean isBudgetExceeded(BigDecimal spent, BigDecimal budgetAmount) {
        if (spent == null) {
            throw new IllegalArgumentException("Spent amount cannot be null");
        }
        if (budgetAmount == null) {
            throw new IllegalArgumentException("Budget amount cannot be null");
        }
        
        return spent.compareTo(budgetAmount) > 0;
    }

    // ================================
    // GOAL CALCULATIONS
    // ================================

    /**
     * Calculates the goal completion percentage.
     * 
     * @param currentAmount Current saved amount
     * @param targetAmount Target goal amount
     * @return Completion percentage (0-100+)
     * @throws IllegalArgumentException if any parameter is null or target amount is zero
     */
    public static BigDecimal calculateGoalProgress(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (currentAmount == null) {
            throw new IllegalArgumentException("Current amount cannot be null");
        }
        if (targetAmount == null) {
            throw new IllegalArgumentException("Target amount cannot be null");
        }
        if (targetAmount.compareTo(ZERO) == 0) {
            throw new IllegalArgumentException("Target amount cannot be zero");
        }
        
        return currentAmount.divide(targetAmount, PERCENTAGE_SCALE, DEFAULT_ROUNDING)
                .multiply(ONE_HUNDRED)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculates the remaining amount needed to reach a goal.
     * 
     * @param targetAmount Target goal amount
     * @param currentAmount Current saved amount
     * @return Remaining amount needed (can be negative if goal exceeded)
     * @throws IllegalArgumentException if any parameter is null
     */
    public static BigDecimal calculateRemainingGoalAmount(BigDecimal targetAmount, BigDecimal currentAmount) {
        if (targetAmount == null) {
            throw new IllegalArgumentException("Target amount cannot be null");
        }
        if (currentAmount == null) {
            throw new IllegalArgumentException("Current amount cannot be null");
        }
        
        return targetAmount.subtract(currentAmount)
                .setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Calculates the monthly savings needed to reach a goal by a target date.
     * 
     * @param remainingAmount Amount still needed to reach goal
     * @param targetDate Target completion date
     * @return Monthly savings amount needed
     * @throws IllegalArgumentException if any parameter is null or target date is in the past
     */
    public static BigDecimal calculateMonthlySavingsNeeded(BigDecimal remainingAmount, LocalDateTime targetDate) {
        if (remainingAmount == null) {
            throw new IllegalArgumentException("Remaining amount cannot be null");
        }
        if (targetDate == null) {
            throw new IllegalArgumentException("Target date cannot be null");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (targetDate.isBefore(now)) {
            throw new IllegalArgumentException("Target date cannot be in the past");
        }
        
        long monthsRemaining = ChronoUnit.MONTHS.between(now, targetDate);
        if (monthsRemaining == 0) {
            monthsRemaining = 1; // At least one month to avoid division by zero
        }
        
        return remainingAmount.divide(BigDecimal.valueOf(monthsRemaining), MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    // ================================
    // UTILITY METHODS
    // ================================

    /**
     * Safely converts a BigDecimal to a properly scaled monetary amount.
     * 
     * @param amount The amount to convert
     * @return Properly scaled BigDecimal or ZERO if input is null
     */
    public static BigDecimal toMonetaryAmount(BigDecimal amount) {
        if (amount == null) {
            return ZERO;
        }
        return amount.setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Checks if an amount is positive (greater than zero).
     * 
     * @param amount The amount to check
     * @return true if amount is positive, false otherwise
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) > 0;
    }

    /**
     * Checks if an amount is negative (less than zero).
     * 
     * @param amount The amount to check
     * @return true if amount is negative, false otherwise
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(ZERO) < 0;
    }

    /**
     * Returns the absolute value of a monetary amount.
     * 
     * @param amount The amount to get absolute value for
     * @return Absolute value with proper monetary scale
     * @throws IllegalArgumentException if amount is null
     */
    public static BigDecimal abs(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return amount.abs().setScale(MONETARY_SCALE, DEFAULT_ROUNDING);
    }
}
