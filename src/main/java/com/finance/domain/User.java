package com.finance.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User Entity - Represents a user in the Personal Finance Management System
 *
 * This entity encapsulates all user-related information including authentication details,
 * profile information, and relationships to financial data (transactions, budgets, goals).
 *
 * Key Features:
 * - Unique username and email constraints for authentication
 * - Role-based access control integration
 * - Bidirectional relationships with financial entities
 * - Automatic timestamp management for audit trails
 * - Helper methods for managing entity relationships
 *
 * Security Considerations:
 * - Password field should always contain encrypted/hashed values
 * - Email and username uniqueness is enforced at database level
 * - Role-based authorization is handled through the userRole relationship
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_created_at", columnList = "created_at")
})
public class User {

    /**
     * Primary key identifier for the user.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Unique username for user authentication and identification.
     * Must be between 3-50 characters and is required for all users.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * User's email address for communication and alternative login.
     * Must be a valid email format and unique across all users.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * Encrypted password for user authentication.
     * Minimum 6 characters required. Should always be stored as BCrypt hash.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User's role determining access permissions and capabilities.
     * Eagerly fetched to support authorization decisions.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role userRole;

    /**
     * Timestamp when the user account was created.
     * Automatically set during entity creation for audit purposes.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * All financial transactions associated with this user.
     * Cascade operations ensure data integrity when user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    /**
     * All budget plans created by this user.
     * Orphan removal ensures cleanup of budgets when user is deleted.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Budget> budgets = new ArrayList<>();

    /**
     * All savings goals set by this user.
     * Maintains referential integrity through cascade operations.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Goal> goals = new ArrayList<>();

    // ================================
    // CONSTRUCTORS
    // ================================

    /**
     * Default constructor required by JPA.
     * Initializes the createdAt timestamp to current time.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Convenience constructor for creating a new user with basic information.
     *
     * @param username The unique username for the user
     * @param email The user's email address
     * @param password The raw password (will be encrypted by service layer)
     */
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // ================================
    // GETTERS AND SETTERS
    // ================================

    /**
     * Gets the user's unique identifier.
     * @return The user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     * @param id The user ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's username.
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's email address.
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's encrypted password.
     * @return The encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * Note: This should always be an encrypted/hashed password.
     * @param password The encrypted password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's role for authorization purposes.
     * @return The user's role
     */
    public Role getUserRole() {
        return userRole;
    }

    /**
     * Sets the user's role.
     * @param userRole The role to assign to the user
     */
    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets the timestamp when the user account was created.
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the account creation timestamp.
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets all transactions associated with this user.
     * @return List of user's transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Sets the user's transactions list.
     * @param transactions The transactions list to set
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Gets all budgets created by this user.
     * @return List of user's budgets
     */
    public List<Budget> getBudgets() {
        return budgets;
    }

    /**
     * Sets the user's budgets list.
     * @param budgets The budgets list to set
     */
    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    /**
     * Gets all goals set by this user.
     * @return List of user's goals
     */
    public List<Goal> getGoals() {
        return goals;
    }

    /**
     * Sets the user's goals list.
     * @param goals The goals list to set
     */
    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    // ================================
    // BUSINESS LOGIC METHODS
    // ================================

    /**
     * Adds a transaction to this user's transaction list.
     * Maintains bidirectional relationship integrity.
     *
     * @param transaction The transaction to add
     * @throws IllegalArgumentException if transaction is null
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.add(transaction);
        transaction.setUser(this);
    }

    /**
     * Removes a transaction from this user's transaction list.
     * Maintains bidirectional relationship integrity.
     *
     * @param transaction The transaction to remove
     */
    public void removeTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.remove(transaction);
            transaction.setUser(null);
        }
    }

    /**
     * Adds a budget to this user's budget list.
     * Maintains bidirectional relationship integrity.
     *
     * @param budget The budget to add
     * @throws IllegalArgumentException if budget is null
     */
    public void addBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget cannot be null");
        }
        budgets.add(budget);
        budget.setUser(this);
    }

    /**
     * Removes a budget from this user's budget list.
     * Maintains bidirectional relationship integrity.
     *
     * @param budget The budget to remove
     */
    public void removeBudget(Budget budget) {
        if (budget != null) {
            budgets.remove(budget);
            budget.setUser(null);
        }
    }

    /**
     * Adds a goal to this user's goal list.
     * Maintains bidirectional relationship integrity.
     *
     * @param goal The goal to add
     * @throws IllegalArgumentException if goal is null
     */
    public void addGoal(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        goals.add(goal);
        goal.setUser(this);
    }

    /**
     * Removes a goal from this user's goal list.
     * Maintains bidirectional relationship integrity.
     *
     * @param goal The goal to remove
     */
    public void removeGoal(Goal goal) {
        if (goal != null) {
            goals.remove(goal);
            goal.setUser(null);
        }
    }

    /**
     * Checks if this user has administrative privileges.
     *
     * @return true if user has ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        return this.userRole != null && "ADMIN".equals(this.userRole.getName());
    }

    /**
     * Checks if this user has regular user privileges.
     *
     * @return true if user has USER role, false otherwise
     */
    public boolean isRegularUser() {
        return this.userRole != null && "USER".equals(this.userRole.getName());
    }

    // ================================
    // OBJECT METHODS
    // ================================

    /**
     * Compares this user with another object for equality.
     * Two users are considered equal if they have the same ID.
     *
     * @param obj The object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    /**
     * Generates hash code for this user based on ID.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this user.
     * Does not include sensitive information like password.
     *
     * @return String representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userRole=" + (userRole != null ? userRole.getName() : "null") +
                ", createdAt=" + createdAt +
                ", transactionCount=" + transactions.size() +
                ", budgetCount=" + budgets.size() +
                ", goalCount=" + goals.size() +
                '}';
    }
}