package com.finance.domain;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal name is required")
    @Size(max = 100, message = "Goal name must not exceed 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "1", message = "Target amount must be greater than 0")
    @Column(name = "target_amount", nullable = false, precision = 19, scale = 0)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", precision = 19, scale = 0)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String description;


    @Column(name = "last_notification_percentage")
    private Integer lastNotificationPercentage;
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Goal() {
        this.createdAt = LocalDateTime.now();
    }

    public Goal(String name, BigDecimal targetAmount, LocalDate targetDate, User user) {
        this();
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getLastNotificationPercentage() {
        return lastNotificationPercentage;
    }

    public void setLastNotificationPercentage(Integer lastNotificationPercentage) {
        this.lastNotificationPercentage = lastNotificationPercentage;
    }

    // Business logic methods
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }

    public double getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount.doubleValue() / targetAmount.doubleValue() * 100;
    }

    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }

    public long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(targetDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(today, targetDate);
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(targetDate) && !isCompleted();
    }

    public void markAsCompleted() {
        this.status = GoalStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void addProgress(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (isCompleted()) {
            markAsCompleted();
        }
    }

    @Override
    public String toString() {
        return name + " - " + currentAmount + "/" + targetAmount + " (" +
               String.format("%.1f", getProgressPercentage()) + "%)";
    }

    public enum GoalStatus {
        ACTIVE("Active"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        GoalStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}