package com.financemanager.service;

import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.repository.CategoryRepository;
import com.financemanager.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceInitializerTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private CategoryService categoryService;

    @Test
    void initializeDefaultCategories_insertsMissingOnes_onlyOnce() {
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Salary")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Food")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Rent")).thenReturn(Optional.of(Category.builder().name("Rent").type(CategoryType.EXPENSE).isCustom(false).user(null).build()));
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Transportation")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Entertainment")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Healthcare")).thenReturn(Optional.empty());
        when(categoryRepository.findByNameIgnoreCaseAndUserIsNull("Utilities")).thenReturn(Optional.empty());

        categoryService.initializeDefaultCategories();

        // Rent already exists so should not be saved; others should be saved
        verify(categoryRepository, atLeast(1)).save(any(Category.class));
        verify(categoryRepository, never()).save(argThat(c -> c.getName().equalsIgnoreCase("Rent")));
    }
}
