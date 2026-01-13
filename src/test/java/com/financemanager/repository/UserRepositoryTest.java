package com.financemanager.repository;

import com.financemanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    @Test
    void findByUsername_andExists() {
        User saved = userRepository.save(User.builder()
                .username("repo@example.com")
                .password("pass")
                .fullName("Repo User")
                .phoneNumber("+1234567890")
                .build());

        Optional<User> found = userRepository.findByUsername("repo@example.com");
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertTrue(userRepository.existsByUsername("repo@example.com"));
        assertFalse(userRepository.existsByUsername("nope@example.com"));
    }
}
