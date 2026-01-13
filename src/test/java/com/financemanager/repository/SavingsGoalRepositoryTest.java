package com.financemanager.repository;

import com.financemanager.entity.SavingsGoal;
import com.financemanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SavingsGoalRepositoryTest {

    @Autowired private SavingsGoalRepository savingsGoalRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void findByUserOrderByTargetDateAsc_ordersByTargetDate() {
        User user = userRepository.save(User.builder()
                .username("g@e.com")
                .password("p")
                .fullName("G")
                .phoneNumber("+1")
                .build());

        savingsGoalRepository.save(SavingsGoal.builder()
                .goalName("G1")
                .targetAmount(new BigDecimal("100"))
                .targetDate(LocalDate.now().plusMonths(2))
                .startDate(LocalDate.now())
                .user(user)
                .build());
        savingsGoalRepository.save(SavingsGoal.builder()
                .goalName("G2")
                .targetAmount(new BigDecimal("200"))
                .targetDate(LocalDate.now().plusMonths(1))
                .startDate(LocalDate.now())
                .user(user)
                .build());

        List<SavingsGoal> list = savingsGoalRepository.findByUserOrderByTargetDateAsc(user);
        assertEquals(2, list.size());
        assertTrue(list.get(0).getTargetDate().isBefore(list.get(1).getTargetDate()));
    }
}
