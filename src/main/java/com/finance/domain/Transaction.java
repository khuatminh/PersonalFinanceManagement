package com.finance.domain;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction Entity - Represents a financial transaction in the system
 *
 * This entity encapsulates all information related to financial transactions including
 * income and expense records. Each transaction is associated with a user and category
 * for proper organization and reporting.
 *
 * Key Features:
 * - Support for both income and expense transactions
 * - Precise decimal handling for monetary amounts using BigDecimal
 * - Automatic timestamp management for audit trails
 * - Category-based organization for reporting and analysis
 * - Flexible notes field for additional transaction details
 *
 * Business Rules:
 * - All amounts must be positive (negative values handled by transaction type)
 * - Each transaction must belong to exactly one user and one category
 * - Transaction dates can be different from creation dates (for backdated entries)
 * - Descriptions are required and limited to 200 characters for consistency
 *
 * @author Personal Finance Manager Team
 * @version 1.0.0
 * @since 2024
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_user", columnList = "user_id"),
    @Index(name = "idx_transaction_category", columnList = "category_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_type", columnList = "type"),
    @Index(name = "idx_transaction_created_at", columnList = "created_at")
})
public class Transaction {

    /**
     * Primary key identifier for the transaction.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Brief description of the transaction.
     * Required field with maximum length of 200 characters.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    /**
     * Transaction amount in the system's base currency.
     * Uses BigDecimal for precise monetary calculations.
     * Must be positive (sign determined by transaction type).
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Type of transaction (INCOME or EXPENSE).
     * Determines how the amount affects the user's balance.
     */
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TransactionType type;

    /**
     * Date when the transaction actually occurred.
     * Can be different from creation date for backdated entries.
     */
    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    /**
     * Timestamp when the transaction record was created in the system.
     * Automatically set during entity creation for audit purposes.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Optional additional notes or details about the transaction.
     * Useful for storing receipts, references, or explanations.
     */
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * The user who owns this transaction.
     * Lazy-loaded to optimize performance when not needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The category this transaction belongs to.
     * Used for organization, budgeting, and reporting.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // ================================
    // CONSTRUCTORS
    // ================================

    /**
     * Default constructor required by JPA.
     * Initializes timestamps to current time.
     */
    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.transactionDate = LocalDateTime.now();
    }

    /**
     * Convenience constructor for creating a transaction with essential information.
     *
     * @param description Brief description of the transaction
     * @param amount Transaction amount (must be positive)
     * @param type Transaction type (INCOME or EXPENSE)
     * @param user The user who owns this transaction
     * @param category The category this transaction belongs to
     */
    public Transaction(String description, BigDecimal amount, TransactionType type, User user, Category category) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.user = user;
        this.category = category;
    }

    // ================================
    // GETTERS AND SETTERS
    // ================================

    /**
     * Gets the transaction's unique identifier.
     * @return The transaction ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the transaction's unique identifier.
     * @param id The transaction ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the transaction description.
     * @return The transaction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the transaction description.
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the transaction amount.
     * @return The transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the transaction amount.
     * @param amount The amount to set (must be positive)
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the transaction type.
     * @return The transaction type (INCOME or EXPENSE)
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the transaction type.
     * @param type The transaction type to set
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * Gets the date when the transaction occurred.
     * @return The transaction date
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the date when the transaction occurred.
     * @param transactionDate The transaction date to set
     */
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the timestamp when the record was created.
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the optional transaction notes.
     * @return The transaction notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the optional transaction notes.
     * @param notes The notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the user who owns this transaction.
     * @return The transaction owner
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns this transaction.
     * @param user The user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the category this transaction belongs to.
     * @return The transaction category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category this transaction belongs to.
     * @param category The category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    // ================================
    // BUSINESS LOGIC METHODS
    // ================================

    /**
     * Checks if this is an income transaction.
     * @return true if transaction type is INCOME, false otherwise
     */
    public boolean isIncome() {
        return TransactionType.INCOME.equals(this.type);
    }

    /**
     * Checks if this is an expense transaction.
     * @return true if transaction type is EXPENSE, false otherwise
     */
    public boolean isExpense() {
        return TransactionType.EXPENSE.equals(this.type);
    }

    /**
     * Gets the signed amount based on transaction type.
     * Income transactions return positive amounts, expenses return negative amounts.
     * @return The signed amount for balance calculations
     */
    public BigDecimal getSignedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    // ================================
    // OBJECT METHODS
    // ================================

    /**
     * Compares this transaction with another object for equality.
     * Two transactions are considered equal if they have the same ID.
     *
     * @param obj The object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return Objects.equals(id, that.id);
    }

    /**
     * Generates hash code for this transaction based on ID.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this transaction.
     *
     * @return String representation including description, amount, and type
     */
    @Override
    public String toString() {
        return String.format("Transaction{id=%d, description='%s', amount=%s, type=%s, date=%s}",
                id, description, amount, type, transactionDate);
    }

    // ================================
    // NESTED ENUMS
    // ================================

    /**
     * Enumeration of transaction types supported by the system.
     *
     * This enum defines the two fundamental types of financial transactions:
     * - INCOME: Money coming into the user's account (positive impact on balance)
     * - EXPENSE: Money going out of the user's account (negative impact on balance)
     *
     * Each type has a display name for user interface presentation.
     */
    public enum TransactionType {
        /**
         * Income transaction - increases user's balance.
         * Examples: salary, freelance payment, investment returns, gifts received.
         */
        INCOME("Income"),

        /**
         * Expense transaction - decreases user's balance.
         * Examples: rent, groceries, utilities, entertainment, purchases.
         */
        EXPENSE("Expense");

        private final String displayName;

        /**
         * Constructor for TransactionType enum.
         *
         * @param displayName Human-readable name for UI display
         */
        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Gets the display name for this transaction type.
         *
         * @return The human-readable display name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Checks if this transaction type represents income.
         *
         * @return true if this is INCOME type, false otherwise
         */
        public boolean isIncome() {
            return this == INCOME;
        }

        /**
         * Checks if this transaction type represents an expense.
         *
         * @return true if this is EXPENSE type, false otherwise
         */
        public boolean isExpense() {
            return this == EXPENSE;
        }
    }
}