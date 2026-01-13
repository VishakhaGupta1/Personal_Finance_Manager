package com.financemanager.controller;

import com.financemanager.dto.CategoriesResponse;
import com.financemanager.dto.CategoryResponse;
import com.financemanager.dto.CreateCategoryRequest;
import com.financemanager.dto.MessageResponse;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for category management endpoints.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthenticationService authenticationService;

    /**
     * Get all categories available to the user.
     *
     * @return CategoriesResponse with all categories
     */
    @GetMapping
    public ResponseEntity<CategoriesResponse> getAllCategories() {
        var user = authenticationService.getCurrentUser();
        CategoriesResponse response = categoryService.getAllCategories(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a custom category.
     *
     * @param request Create category request
     * @return CategoryResponse with created category
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        var user = authenticationService.getCurrentUser();
        CategoryResponse response = categoryService.createCustomCategory(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a custom category.
     *
     * @param name Category name
     * @return MessageResponse with success message
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable String name) {
        var user = authenticationService.getCurrentUser();
        categoryService.deleteCustomCategory(name, user);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Category deleted successfully")
                .build());
    }
}
