package com.finance.utils;

import java.util.Arrays;
import java.util.List;

public class ColorUtils {

    private static final List<String> EXPENSE_COLORS = Arrays.asList(
        "#ef4444", "#f97316", "#f59e0b", "#eab308", "#84cc16",
        "#22c55e", "#10b981", "#14b8a6", "#06b6d4", "#0ea5e9",
        "#3b82f6", "#6366f1", "#8b5cf6", "#a855f7", "#d946ef"
    );

    private static final List<String> INCOME_COLORS = Arrays.asList(
        "#10b981", "#059669", "#047857", "#065f46", "#064e3b",
        "#22c55e", "#16a34a", "#15803d", "#166534", "#14532d",
        "#84cc16", "#65a30d", "#4d7c0f", "#365314", "#1a2e05"
    );

    public static String getColor(int index, String type) {
        if ("expense".equalsIgnoreCase(type)) {
            return EXPENSE_COLORS.get(index % EXPENSE_COLORS.size());
        }
        return INCOME_COLORS.get(index % INCOME_COLORS.size());
    }
}
