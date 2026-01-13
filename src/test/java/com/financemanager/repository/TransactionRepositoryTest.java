package com.financemanager.repository;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.Transaction;
import com.financemanager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    private User user;
    private Category income;
    private Category expense;

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder()
                .username("u@example.com")
                .password("pass")
                .fullName("User")
                .phoneNumber("+1234567890")
                .build());

        income = categoryRepository.save(Category.builder()
                .name("Salary")
                .type(CategoryType.INCOME)
                .isCustom(false)
                .user(null)
                .build());

        expense = categoryRepository.save(Category.builder()
                .name("Food")
                .type(CategoryType.EXPENSE)
                .isCustom(true)
                .user(user)
                .build());

        transactionRepository.save(Transaction.builder()
                .amount(new BigDecimal("100"))
                .date(LocalDate.now().minusDays(3))
                .description("Breakfast")
                .category(expense)
                .user(user)
                .build());
        transactionRepository.save(Transaction.builder()
                .amount(new BigDecimal("1000"))
                .date(LocalDate.now().minusDays(2))
                .description("Salary")
                .category(income)
                .user(user)
                .build());
        transactionRepository.save(Transaction.builder()
                .amount(new BigDecimal("50"))
                .date(LocalDate.now().minusDays(1))
                .description("Lunch")
                .category(expense)
                .user(user)
                .build());
    }

    @Test
    void findByUserOrderByDateDesc_returnsOrdered() {
        List<Transaction> list = transactionRepository.findByUserOrderByDateDesc(user);
        assertEquals(3, list.size());
        assertTrue(list.get(0).getDate().isAfter(list.get(1).getDate()));
    }

    @Test
    void findByUserAndDateRange_filtersByRange() {
        var start = LocalDate.now().minusDays(3);
        var end = LocalDate.now().minusDays(2);
        List<Transaction> list = transactionRepository.findByUserAndDateRange(user, start, end);
        assertEquals(2, list.size());
    }

    @Test
    void findByUserAndCategory_filtersByCategory() {
        List<Transaction> list = transactionRepository.findByUserAndCategory(user, expense.getId());
        assertEquals(2, list.size());
        assertEquals(CategoryType.EXPENSE, list.get(0).getCategory().getType());
    }

    @Test
    void findByUserDateRangeAndCategory_filtersBoth() {
        var start = LocalDate.now().minusDays(4);
        var end = LocalDate.now();
        List<Transaction> list = transactionRepository.findByUserDateRangeAndCategory(user, start, end, expense.getId());
        assertEquals(2, list.size());
    }

    @Test
    void findByUserAndTypeOrderByDateDesc_filtersByType() {
        List<Transaction> list = transactionRepository.findByUserAndTypeOrderByDateDesc(user, CategoryType.INCOME);
        assertEquals(1, list.size());
        assertEquals(CategoryType.INCOME, list.get(0).getCategory().getType());
    }

    @Test
    void findByUserAndDateRangeAndType_filtersByBoth() {
        var start = LocalDate.now().minusDays(4);
        var end = LocalDate.now();
        List<Transaction> list = transactionRepository.findByUserAndDateRangeAndType(user, start, end, CategoryType.EXPENSE);
        assertEquals(2, list.size());
    }

    @Test
    void countByUserAndCategoryId_countsCorrectly() {
        long count = transactionRepository.countByUserAndCategoryId(user, expense.getId());
        assertEquals(2L, count);
    }
}
