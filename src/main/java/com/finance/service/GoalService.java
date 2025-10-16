package com.finance.service;

import com.finance.domain.Goal;
import com.finance.domain.User;
import com.finance.repository.GoalRepository;
import com.finance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Goal> findAll() {
        return goalRepository.findAll();
    }

    public Optional<Goal> findById(Long id) {
        return goalRepository.findById(id);
    }

    public List<Goal> findByUser(User user) {
        return goalRepository.findByUser(user);
    }

    public List<Goal> findByUserOrderByTargetDateAsc(User user) {
        return goalRepository.findByUserOrderByTargetDateAsc(user);
    }

    public List<Goal> findByUserAndStatus(User user, Goal.GoalStatus status) {
        return goalRepository.findByUserAndStatus(user, status);
    }

    public List<Goal> findActiveGoalsByUser(User user) {
        return goalRepository.findActiveGoalsByUser(user, LocalDate.now());
    }

    public List<Goal> findCompletedGoalsByUser(User user) {
        return goalRepository.findCompletedGoalsByUser(user);
    }

    public List<Goal> findOverdueGoalsByUser(User user) {
        return goalRepository.findOverdueGoalsByUser(user, LocalDate.now());
    }

    public List<Goal> searchGoals(User user, String keyword) {
        return goalRepository.findByUserAndNameContaining(user, keyword);
    }

    public List<Goal> findGoalsTargetingDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return goalRepository.findGoalsTargetingDateRange(user, startDate, endDate);
    }

    public Goal save(Goal goal) {
        return goalRepository.save(goal);
    }

    public Goal createGoal(String name, BigDecimal targetAmount, LocalDate targetDate,
                          User user, String description) {
        // Validate target date is in the future
        if (targetDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Target date cannot be in the past");
        }

        if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than zero");
        }

        Goal goal = new Goal();
        goal.setName(name);
        goal.setTargetAmount(targetAmount);
        goal.setTargetDate(targetDate);
        goal.setUser(user);
        goal.setDescription(description);
        goal.setStatus(Goal.GoalStatus.ACTIVE);
        goal.setCurrentAmount(BigDecimal.ZERO);

        return goalRepository.save(goal);
    }



    public Goal updateGoal(Long id, String name, BigDecimal targetAmount, LocalDate targetDate, String description) {
        return goalRepository.findById(id)
                .map(goal -> {
                    // Validate target date is in the future for active goals
                    if (goal.getStatus() == Goal.GoalStatus.ACTIVE &&
                        targetDate.isBefore(LocalDate.now())) {
                        throw new RuntimeException("Target date cannot be in the past for active goals");
                    }

                    if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new RuntimeException("Target amount must be greater than zero");
                    }

                    goal.setName(name);
                    goal.setTargetAmount(targetAmount);
                    goal.setTargetDate(targetDate);
                    goal.setDescription(description);

                    // Check if goal should be marked as completed
                    if (goal.getCurrentAmount().compareTo(targetAmount) >= 0) {
                        goal.markAsCompleted();
                    }

                    return goalRepository.save(goal);
                })
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
    }

    public void completeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        goal.markAsCompleted();
        goalRepository.save(goal);
    }

    public void contributeToGoal(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Contribution amount must be greater than zero");
        }

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        if (goal.getStatus() != Goal.GoalStatus.ACTIVE) {
            throw new RuntimeException("Can only contribute to active goals");
        }

        goal.addProgress(amount);

        // Check if goal is now completed
        if (goal.isCompleted()) {
            goal.markAsCompleted();
        }

        checkAndSendGoalNotifications(goal);
        goalRepository.save(goal);
    }

    public void deleteById(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found with id: " + id);
        }
        goalRepository.deleteById(id);
    }

    public void addToGoalProgress(Long goalId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        goal.addProgress(amount);
        checkAndSendGoalNotifications(goal);
        goalRepository.save(goal);
    }

    public void setGoalProgress(Long goalId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount cannot be negative");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        goal.setCurrentAmount(amount);
        if (goal.isCompleted()) {
            goal.markAsCompleted();
        }
        checkAndSendGoalNotifications(goal);
        goalRepository.save(goal);
    }

    public void markGoalAsCompleted(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        goal.markAsCompleted();
        goalRepository.save(goal);
    }

    public void cancelGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        goal.setStatus(Goal.GoalStatus.CANCELLED);
        goalRepository.save(goal);
    }

    public void reactivateGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        if (goal.getTargetDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot reactivate a goal with a past target date");
        }

        goal.setStatus(Goal.GoalStatus.ACTIVE);
        if (goal.isCompleted()) {
            goal.setCompletedAt(null);
        }
        goalRepository.save(goal);
    }

    // Statistics and summary methods
    public long getGoalCountByUserAndStatus(User user, Goal.GoalStatus status) {
        return goalRepository.countGoalsByUserAndStatus(user, status);
    }

    public long getActiveGoalCount(User user) {
        return getGoalCountByUserAndStatus(user, Goal.GoalStatus.ACTIVE);
    }

    public long getCompletedGoalCount(User user) {
        return getGoalCountByUserAndStatus(user, Goal.GoalStatus.COMPLETED);
    }

    public long getCancelledGoalCount(User user) {
        return getGoalCountByUserAndStatus(user, Goal.GoalStatus.CANCELLED);
    }

    public long getOverdueGoalCount(User user) {
        return goalRepository.countOverdueGoalsByUser(user, LocalDate.now());
    }

    public BigDecimal getTotalTargetAmountsByUser(User user) {
        BigDecimal total = goalRepository.sumTargetAmountsByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalCurrentAmountsByUser(User user) {
        BigDecimal total = goalRepository.sumCurrentAmountsByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalRemainingAmountsByUser(User user) {
        return getTotalTargetAmountsByUser(user).subtract(getTotalCurrentAmountsByUser(user));
    }

    public GoalSummary getGoalSummary(User user) {
        List<Goal> activeGoals = findActiveGoalsByUser(user);

        BigDecimal totalTarget = BigDecimal.ZERO;
        BigDecimal totalCurrent = BigDecimal.ZERO;
        long nearCompletion = 0;
        long overdue = 0;

        for (Goal goal : activeGoals) {
            totalTarget = totalTarget.add(goal.getTargetAmount());
            totalCurrent = totalCurrent.add(goal.getCurrentAmount());

            if (goal.getProgressPercentage() >= 80) {
                nearCompletion++;
            }
            if (goal.isOverdue()) {
                overdue++;
            }
        }

        double overallProgress = totalTarget.compareTo(BigDecimal.ZERO) > 0 ?
            totalCurrent.doubleValue() / totalTarget.doubleValue() * 100 : 0.0;

        return new GoalSummary(
            activeGoals.size(),
            totalTarget,
            totalCurrent,
            overallProgress,
            nearCompletion,
            overdue,
            getCompletedGoalCount(user)
        );
    }

    public void checkAndUpdateCompletedGoals() {
        List<Goal> goalsReadyToComplete = goalRepository.findGoalsReadyToComplete();
        goalsReadyToComplete.forEach(goal -> {
            goal.markAsCompleted();
            goalRepository.save(goal);
        });
    }

    public void deleteAllByUser(User user) {
        List<Goal> goals = findByUser(user);
        goals.forEach(goal -> goalRepository.delete(goal));
    }

    private void checkAndSendGoalNotifications(Goal goal) {
        double percentage = goal.getProgressPercentage();
        Integer lastNotificationPercentage = goal.getLastNotificationPercentage();

        int currentThreshold = 0;
        if (percentage >= 100) {
            currentThreshold = 100;
        } else if (percentage >= 75) {
            currentThreshold = 75;
        } else if (percentage >= 50) {
            currentThreshold = 50;
        }

        if (currentThreshold > 0 && (lastNotificationPercentage == null || currentThreshold > lastNotificationPercentage)) {
            String message;
            if (currentThreshold >= 100) {
                message = String.format("Congratulations! You have reached your goal '%s'!", goal.getName());
            } else {
                message = String.format("You have reached over %d%% of your goal '%s'. Keep going!", currentThreshold, goal.getName());
            }
            notificationService.createNotification(goal.getUser(), message);
            goal.setLastNotificationPercentage(currentThreshold);
        }
    }

    // Data class for goal summary
    public static class GoalSummary {
        private final long activeGoalsCount;
        private final BigDecimal totalTargetAmount;
        private final BigDecimal totalCurrentAmount;
        private final double overallProgressPercentage;
        private final long nearCompletionCount;
        private final long overdueCount;
        private final long completedCount;

        public GoalSummary(long activeGoalsCount, BigDecimal totalTargetAmount, BigDecimal totalCurrentAmount,
                         double overallProgressPercentage, long nearCompletionCount, long overdueCount,
                         long completedCount) {
            this.activeGoalsCount = activeGoalsCount;
            this.totalTargetAmount = totalTargetAmount;
            this.totalCurrentAmount = totalCurrentAmount;
            this.overallProgressPercentage = overallProgressPercentage;
            this.nearCompletionCount = nearCompletionCount;
            this.overdueCount = overdueCount;
            this.completedCount = completedCount;
        }

        // Getters
        public long getActiveGoalsCount() { return activeGoalsCount; }
        public BigDecimal getTotalTargetAmount() { return totalTargetAmount; }
        public BigDecimal getTotalCurrentAmount() { return totalCurrentAmount; }
        public double getOverallProgressPercentage() { return overallProgressPercentage; }
        public long getNearCompletionCount() { return nearCompletionCount; }
        public long getOverdueCount() { return overdueCount; }
        public long getCompletedCount() { return completedCount; }
        public BigDecimal getTotalRemainingAmount() {
            return totalTargetAmount.subtract(totalCurrentAmount);
        }
    }
}