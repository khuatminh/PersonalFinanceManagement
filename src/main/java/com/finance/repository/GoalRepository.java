package com.finance.repository;

import com.finance.domain.Goal;
import com.finance.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUser(User user);

    List<Goal> findByUserOrderByTargetDateAsc(User user);

    List<Goal> findByUserAndStatus(User user, Goal.GoalStatus status);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE' AND g.targetDate < :currentDate")
    List<Goal> findOverdueGoalsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE' AND g.targetDate >= :currentDate")
    List<Goal> findActiveGoalsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.status = 'COMPLETED' ORDER BY g.completedAt DESC")
    List<Goal> findCompletedGoalsByUser(@Param("user") User user);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND " +
           "(g.name LIKE %:keyword% OR g.description LIKE %:keyword%)")
    List<Goal> findByUserAndNameContaining(@Param("user") User user, @Param("keyword") String keyword);

    @Query("SELECT g FROM Goal g WHERE g.user = :user AND g.currentAmount < g.targetAmount AND g.targetDate BETWEEN :startDate AND :endDate")
    List<Goal> findGoalsTargetingDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.status = :status")
    long countGoalsByUserAndStatus(@Param("user") User user, @Param("status") Goal.GoalStatus status);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE' AND g.targetDate < :currentDate")
    long countOverdueGoalsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT SUM(g.targetAmount) FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE'")
    java.math.BigDecimal sumTargetAmountsByUser(@Param("user") User user);

    @Query("SELECT SUM(g.currentAmount) FROM Goal g WHERE g.user = :user AND g.status = 'ACTIVE'")
    java.math.BigDecimal sumCurrentAmountsByUser(@Param("user") User user);

    @Query("SELECT g FROM Goal g WHERE g.currentAmount >= g.targetAmount AND g.status != 'COMPLETED'")
    List<Goal> findGoalsReadyToComplete();
}