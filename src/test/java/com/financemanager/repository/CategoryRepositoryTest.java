package com.financemanager.repository;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void findByUserOrUserIsNull_returnsDefaultAndCustom() {
        User user = userRepository.save(User.builder()
                .username("u@e.com")
                .password("p")
                .fullName("U")
                .phoneNumber("+1")
                .build());
        Category def = categoryRepository.save(Category.builder().name("Salary").type(CategoryType.INCOME).isCustom(false).user(null).build());
        Category custom = categoryRepository.save(Category.builder().name("Food").type(CategoryType.EXPENSE).isCustom(true).user(user).build());

        List<Category> list = categoryRepository.findByUserOrUserIsNull(user);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(c -> c.getUser() == null));
        assertTrue(list.stream().anyMatch(c -> c.getUser() != null && c.getUser().getId().equals(user.getId())));
    }

    @Test
    void findByNameIgnoreCaseAndUserIsNull_matchesDefault() {
        categoryRepository.save(Category.builder().name("Utilities").type(CategoryType.EXPENSE).isCustom(false).user(null).build());
        Optional<Category> found = categoryRepository.findByNameIgnoreCaseAndUserIsNull("UTILITIES");
        assertTrue(found.isPresent());
        assertNull(found.get().getUser());
    }

    @Test
    void existsByNameAndUser_checksCustomScope() {
        User user = userRepository.save(User.builder().username("u").password("p").fullName("U").phoneNumber("+1").build());
        categoryRepository.save(Category.builder().name("Transport").type(CategoryType.EXPENSE).isCustom(true).user(user).build());
        assertTrue(categoryRepository.existsByNameAndUser("Transport", user));
        assertFalse(categoryRepository.existsByNameAndUser("Transport", User.builder().id(999L).build()));
    }
}
