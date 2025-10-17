package com.finance.config;

import com.finance.domain.Category;
import com.finance.domain.Role;
import com.finance.domain.Transaction;
import com.finance.domain.User;
import com.finance.repository.CategoryRepository;
import com.finance.repository.RoleRepository;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                    CategoryRepository categoryRepository,
                                    RoleRepository roleRepository,
                                    TransactionRepository transactionRepository,
                                    BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // Initialize default roles if they don't exist
            if (roleRepository.count() == 0) {
                initializeRoles(roleRepository);
            }

            // Initialize default categories if they don't exist
            if (categoryRepository.count() == 0) {
                initializeCategories(categoryRepository);
            }

            // Initialize admin user if it doesn't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                initializeAdminUser(userRepository, roleRepository, passwordEncoder);
            }

            // Create sample transactions for admin user if none exist in the last 30 days
            userRepository.findByUsername("admin").ifPresent(user -> {
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
                if (transactionRepository.countByUserAndTransactionDateAfter(user, thirtyDaysAgo) == 0) {
                    createSampleTransactions(user, categoryRepository, transactionRepository);
                }
            });
        };
    }

    private void initializeCategories(CategoryRepository categoryRepository) {
        // Income categories
        Category salary = new Category("Salary", Category.CategoryType.INCOME, "#28a745");
        Category freelance = new Category("Freelance", Category.CategoryType.INCOME, "#20c997");
        Category investment = new Category("Investment", Category.CategoryType.INCOME, "#6f42c1");
        Category gifts = new Category("Gifts", Category.CategoryType.INCOME, "#e83e8c");
        Category otherIncome = new Category("Other Income", Category.CategoryType.INCOME, "#6c757d");

        // Expense categories
        Category food = new Category("Food & Dining", Category.CategoryType.EXPENSE, "#fd7e14");
        Category transport = new Category("Transportation", Category.CategoryType.EXPENSE, "#007bff");
        Category shopping = new Category("Shopping", Category.CategoryType.EXPENSE, "#dc3545");
        Category entertainment = new Category("Entertainment", Category.CategoryType.EXPENSE, "#6610f2");
        Category bills = new Category("Bills & Utilities", Category.CategoryType.EXPENSE, "#17a2b8");
        Category healthcare = new Category("Healthcare", Category.CategoryType.EXPENSE, "#20c997");
        Category education = new Category("Education", Category.CategoryType.EXPENSE, "#6f42c1");
        Category rent = new Category("Rent/Mortgage", Category.CategoryType.EXPENSE, "#e83e8c");
        Category savings = new Category("Savings", Category.CategoryType.EXPENSE, "#28a745");
        Category otherExpense = new Category("Other Expenses", Category.CategoryType.EXPENSE, "#6c757d");

        categoryRepository.saveAll(Arrays.asList(
            salary, freelance, investment, gifts, otherIncome,
            food, transport, shopping, entertainment, bills,
            healthcare, education, rent, savings, otherExpense
        ));
    }

    private void initializeRoles(RoleRepository roleRepository) {
        if (!roleRepository.existsByName("ADMIN")) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (!roleRepository.existsByName("USER")) {
            roleRepository.save(new Role("USER"));
        }
    }

    private void createSampleTransactions(User user, CategoryRepository categoryRepository, TransactionRepository transactionRepository) {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Cannot create sample transactions.");
            return;
        }

        Category foodCategory = findCategoryByName(categories, "Food & Dining");
        Category transportCategory = findCategoryByName(categories, "Transportation");
        Category salaryCategory = findCategoryByName(categories, "Salary");
        Category entertainmentCategory = findCategoryByName(categories, "Entertainment");
        Category billsCategory = findCategoryByName(categories, "Bills & Utilities");

        LocalDateTime now = LocalDateTime.now();

        // Income
        if (salaryCategory != null) {
            createTransaction(user, salaryCategory, "Monthly Salary", new BigDecimal("5000.00"),
                    Transaction.TransactionType.INCOME, now.minusDays(25), transactionRepository);
            createTransaction(user, salaryCategory, "Freelance Project", new BigDecimal("1200.00"),
                    Transaction.TransactionType.INCOME, now.minusDays(15), transactionRepository);
        }

        // Expenses
        if (foodCategory != null) {
            createTransaction(user, foodCategory, "Grocery Shopping", new BigDecimal("150.75"),
                    Transaction.TransactionType.EXPENSE, now.minusDays(2), transactionRepository);
            createTransaction(user, foodCategory, "Restaurant Dinner", new BigDecimal("85.50"),
                    Transaction.TransactionType.EXPENSE, now.minusDays(5), transactionRepository);
        }

        if (transportCategory != null) {
            createTransaction(user, transportCategory, "Gasoline", new BigDecimal("65.00"),
                    Transaction.TransactionType.EXPENSE, now.minusDays(3), transactionRepository);
        }

        if (entertainmentCategory != null) {
            createTransaction(user, entertainmentCategory, "Movie Tickets", new BigDecimal("28.00"),
                    Transaction.TransactionType.EXPENSE, now.minusDays(10), transactionRepository);
        }

        if (billsCategory != null) {
            createTransaction(user, billsCategory, "Internet Bill", new BigDecimal("59.99"),
                    Transaction.TransactionType.EXPENSE, now.minusDays(12), transactionRepository);
        }

        System.out.println("Sample transactions created for admin user.");
    }

    private Category findCategoryByName(List<Category> categories, String name) {
        return categories.stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private void createTransaction(User user, Category category, String description,
                                 BigDecimal amount, Transaction.TransactionType type,
                                 LocalDateTime transactionDate, TransactionRepository transactionRepository) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTransactionDate(transactionDate);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private void initializeAdminUser(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@finance.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setUserRole(adminRole);

        userRepository.save(admin);
    }
}