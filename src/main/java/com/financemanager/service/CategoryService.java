package com.financemanager.service;

import com.financemanager.dto.CategoriesResponse;
import com.financemanager.dto.CategoryResponse;
import com.financemanager.dto.CreateCategoryRequest;
import com.financemanager.entity.Category;
import com.financemanager.entity.CategoryType;
import com.financemanager.entity.User;
import com.financemanager.exception.DuplicateResourceException;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.exception.ResourceNotFoundException;
import com.financemanager.repository.CategoryRepository;
import com.financemanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for category management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    private static final List<String> DEFAULT_INCOME_CATEGORIES = Arrays.asList("Salary");
    private static final List<String> DEFAULT_EXPENSE_CATEGORIES = Arrays.asList(
            "Food", "Rent", "Transportation", "Entertainment", "Healthcare", "Utilities"
    );

    /**
     * Initialize default categories for the system.
     */
    @Transactional
    public void initializeDefaultCategories() {
        
        DEFAULT_INCOME_CATEGORIES.forEach(name -> {
            if (categoryRepository.findByNameIgnoreCaseAndUserIsNull(name).isEmpty()) {
                Category category = Category.builder()
                        .name(name)
                        .type(CategoryType.INCOME)
                        .isCustom(false)
                        .user(null)
                        .build();
                categoryRepository.save(category);
            }
        });

        
        DEFAULT_EXPENSE_CATEGORIES.forEach(name -> {
            if (categoryRepository.findByNameIgnoreCaseAndUserIsNull(name).isEmpty()) {
                Category category = Category.builder()
                        .name(name)
                        .type(CategoryType.EXPENSE)
                        .isCustom(false)
                        .user(null)
                        .build();
                categoryRepository.save(category);
            }
        });
    }

    /**
     * Get all categories available to the user (default + custom).
     *
     * @param user Current user
     * @return CategoriesResponse with all available categories
     */
    public CategoriesResponse getAllCategories(User user) {
        List<Category> categories = categoryRepository.findByUserOrUserIsNull(user);
        List<CategoryResponse> responses = categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return CategoriesResponse.builder()
                .categories(responses)
                .build();
    }

    /**
     * Create a custom category.
     *
     * @param request Create category request
     * @param user Current user
     * @return CategoryResponse with created category
     * @throws DuplicateResourceException if category name already exists for user
     */
    public CategoryResponse createCustomCategory(CreateCategoryRequest request, User user) {
        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .type(CategoryType.valueOf(request.getType()))
                .isCustom(true)
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return toResponse(savedCategory);
    }

    /**
     * Delete a custom category.
     *
     * @param name Category name
     * @param user Current user
     * @throws ResourceNotFoundException if category not found
     * @throws ForbiddenException if trying to delete default category or other user's category
     * @throws IllegalArgumentException if category has associated transactions
     */
    public void deleteCustomCategory(String name, User user) {
        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + name));

        if (!category.getIsCustom()) {
            throw new ForbiddenException("Cannot delete default category: " + name);
        }

        long transactionCount = transactionRepository.countByUserAndCategoryId(user, category.getId());
        if (transactionCount > 0) {
            throw new IllegalArgumentException("Cannot delete category with associated transactions");
        }

        categoryRepository.delete(category);
    }

    /**
     * Get a category by name for the user.
     *
     * @param name Category name
     * @param user Current user
     * @return Category if found
     * @throws ResourceNotFoundException if category not found
     */
    public Category getCategoryByName(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user)
                .or(() -> categoryRepository.findByNameIgnoreCaseAndUserIsNull(name))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + name));
    }

    /**
     * Get a category by ID accessible to the user (custom-owned or default).
     *
     * @param id Category ID
     * @param user Current user
     * @return Category if accessible
     * @throws ResourceNotFoundException if not found
     * @throws ForbiddenException if category belongs to another user
     */
    public Category getCategoryById(Long id, User user) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to access this category");
        }
        return category;
    }

    /**
     * Convert Category entity to CategoryResponse.
     *
     * @param category Category entity
     * @return CategoryResponse
     */
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .type(category.getType().toString())
                .isCustom(category.getIsCustom())
                .build();
    }
}
