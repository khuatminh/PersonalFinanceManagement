package com.finance.service;

import com.finance.domain.Category;
import com.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public List<Category> findByType(Category.CategoryType type) {
        return categoryRepository.findByType(type);
    }

    public List<Category> findByTypeOrderByName(Category.CategoryType type) {
        return categoryRepository.findByTypeOrderByName(type);
    }

    public Category save(Category category) {
        if (category.getId() == null && categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    public Category createCategory(String name, Category.CategoryType type, String color, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category with name '" + name + "' already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setColor(color != null ? color : "#007bff");
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // Check if name is being changed and if new name already exists
                    if (!category.getName().equals(categoryDetails.getName()) &&
                        categoryRepository.existsByName(categoryDetails.getName())) {
                        throw new RuntimeException("Category with name '" + categoryDetails.getName() + "' already exists");
                    }

                    category.setName(categoryDetails.getName());
                    category.setType(categoryDetails.getType());
                    category.setColor(categoryDetails.getColor());
                    category.setDescription(categoryDetails.getDescription());

                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if category has associated transactions
        long transactionCount = categoryRepository.countTransactionsByCategory(id);
        if (transactionCount > 0) {
            throw new RuntimeException("Cannot delete category with associated transactions. Found " +
                                     transactionCount + " transactions.");
        }

        categoryRepository.deleteById(id);
    }

    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameOrDescriptionContaining(keyword);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    public long getTransactionCount(Long categoryId) {
        return categoryRepository.countTransactionsByCategory(categoryId);
    }

    public List<Category> getIncomeCategories() {
        return findByTypeOrderByName(Category.CategoryType.INCOME);
    }

    public List<Category> getExpenseCategories() {
        return findByTypeOrderByName(Category.CategoryType.EXPENSE);
    }

    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            // Default income categories
            createCategory("Salary", Category.CategoryType.INCOME, "#28a745", "Monthly salary");
            createCategory("Freelance", Category.CategoryType.INCOME, "#20c997", "Freelance work");
            createCategory("Investment", Category.CategoryType.INCOME, "#6f42c1", "Investment returns");
            createCategory("Gifts", Category.CategoryType.INCOME, "#e83e8c", "Received gifts");
            createCategory("Other Income", Category.CategoryType.INCOME, "#6c757d", "Other income sources");

            // Default expense categories
            createCategory("Food & Dining", Category.CategoryType.EXPENSE, "#fd7e14", "Restaurants and groceries");
            createCategory("Transportation", Category.CategoryType.EXPENSE, "#007bff", "Public transport and fuel");
            createCategory("Shopping", Category.CategoryType.EXPENSE, "#dc3545", "Clothing and personal items");
            createCategory("Entertainment", Category.CategoryType.EXPENSE, "#6610f2", "Movies, games, and hobbies");
            createCategory("Bills & Utilities", Category.CategoryType.EXPENSE, "#17a2b8", "Monthly bills and utilities");
            createCategory("Healthcare", Category.CategoryType.EXPENSE, "#20c997", "Medical expenses");
            createCategory("Education", Category.CategoryType.EXPENSE, "#6f42c1", "Courses and learning materials");
            createCategory("Rent/Mortgage", Category.CategoryType.EXPENSE, "#e83e8c", "Housing expenses");
            createCategory("Savings", Category.CategoryType.EXPENSE, "#28a745", "Savings and investments");
            createCategory("Other Expenses", Category.CategoryType.EXPENSE, "#6c757d", "Miscellaneous expenses");
        }
    }
}