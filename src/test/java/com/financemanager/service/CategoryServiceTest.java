package com.financemanager.service;

import com.financemanager.dto.CreateCategoryRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.User;
import com.financemanager.exception.DuplicateResourceException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category customCategory;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .build();

        customCategory = Category.builder()
                .id(1L)
                .name("CustomCategory")
                .type(CategoryType.EXPENSE)
                .isCustom(true)
                .user(user)
                .build();

        createRequest = CreateCategoryRequest.builder()
                .name("CustomCategory")
                .type("EXPENSE")
                .build();
    }

    @Test
    void testCreateCustomCategorySuccess() {
        when(categoryRepository.existsByNameAndUser(createRequest.getName(), user)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);

        var response = categoryService.createCustomCategory(createRequest, user);

        assertNotNull(response);
        assertEquals("CustomCategory", response.getName());
        assertEquals("EXPENSE", response.getType());
        assertTrue(response.getIsCustom());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCustomCategoryDuplicate() {
        when(categoryRepository.existsByNameAndUser(createRequest.getName(), user)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCustomCategory(createRequest, user));
    }

    @Test
    void testDeleteCustomCategorySuccess() {
        when(categoryRepository.findByNameAndUser("CustomCategory", user)).thenReturn(Optional.of(customCategory));
        when(transactionRepository.countByUserAndCategoryId(user, 1L)).thenReturn(0L);

        categoryService.deleteCustomCategory("CustomCategory", user);

        verify(categoryRepository, times(1)).delete(customCategory);
    }

    @Test
    void testDeleteDefaultCategoryForbidden() {
        Category defaultCategory = Category.builder()
                .id(1L)
                .name("Salary")
                .type(CategoryType.INCOME)
                .isCustom(false)
                .user(null)
                .build();

        when(categoryRepository.findByNameAndUser("Salary", user)).thenReturn(Optional.of(defaultCategory));

        assertThrows(ForbiddenException.class, () -> categoryService.deleteCustomCategory("Salary", user));
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryRepository.findByNameAndUser("NonExistent", user)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCustomCategory("NonExistent", user));
    }
}
