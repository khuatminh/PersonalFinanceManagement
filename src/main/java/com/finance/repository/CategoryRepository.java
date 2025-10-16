package com.finance.repository;

import com.finance.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findByType(Category.CategoryType type);

    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Category> findByNameOrDescriptionContaining(@Param("keyword") String keyword);

    @Query("SELECT c FROM Category c WHERE c.type = :type ORDER BY c.name")
    List<Category> findByTypeOrderByName(@Param("type") Category.CategoryType type);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.category.id = :categoryId")
    long countTransactionsByCategory(@Param("categoryId") Long categoryId);
}