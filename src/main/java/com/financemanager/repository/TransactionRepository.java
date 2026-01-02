package com.financemanager.repository;

import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDesc(User user);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByUserAndDateRange(@Param("user") User user, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.category.id = :categoryId ORDER BY t.date DESC")
    List<Transaction> findByUserAndCategory(@Param("user") User user, @Param("categoryId") Long categoryId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :startDate AND :endDate AND t.category.id = :categoryId ORDER BY t.date DESC")
    List<Transaction> findByUserDateRangeAndCategory(@Param("user") User user, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate, 
                                                     @Param("categoryId") Long categoryId);
    
    long countByUserAndCategoryId(User user, Long categoryId);
}
