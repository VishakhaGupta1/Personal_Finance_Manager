package com.financemanager.repository;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUser(String name, User user);
    
    @Query("SELECT c FROM Category c WHERE c.user = :user OR c.user IS NULL")
    List<Category> findByUserOrUserIsNull(@Param("user") User user);
    
    List<Category> findByUser(User user);
    Optional<Category> findByNameIgnoreCaseAndUserIsNull(String name);
    boolean existsByNameAndUser(String name, User user);
    long countByUserAndId(User user, Long categoryId);
}
