package com.financemanager.service;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.User;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.repository.CategoryRepository;
import com.financemanager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceExtraTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).username("u").build();
    }

    @Test
    void deleteCustomCategory_withReferences_throws() {
        Category custom = Category.builder().id(9L).name("C").type(CategoryType.EXPENSE).isCustom(true).user(user).build();
        when(categoryRepository.findByNameAndUser("C", user)).thenReturn(Optional.of(custom));
        when(transactionRepository.countByUserAndCategoryId(user, 9L)).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCustomCategory("C", user));
    }

    @Test
    void getCategoryByName_fallsBackToDefault() {
        Category def = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).isCustom(false).user(null).build();
        when(categoryRepository.findByNameAndUser("Salary", user)).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Salary")).thenReturn(Optional.of(def));
        var c = categoryService.getCategoryByName("Salary", user);
        assertEquals("Salary", c.getName());
        assertNull(c.getUser());
    }

    @Test
    void getCategoryByName_notFound_throws() {
        when(categoryRepository.findByNameAndUser("X", user)).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName("X", user));
    }

    @Test
    void getCategoryById_otherUsersCustom_forbidden() {
        User other = User.builder().id(2L).username("o").build();
        Category custom = Category.builder().id(5L).name("C").type(CategoryType.EXPENSE).isCustom(true).user(other).build();
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(custom));
        assertThrows(ForbiddenException.class, () -> categoryService.getCategoryById(5L, user));
    }

    @Test
    void getCategoryById_default_ok() {
        Category def = Category.builder().id(1L).name("Salary").type(CategoryType.INCOME).isCustom(false).user(null).build();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(def));
        var c = categoryService.getCategoryById(1L, user);
        assertEquals("Salary", c.getName());
    }
}
