package com.financemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financemanager.dto.CategoriesResponse;
import com.financemanager.dto.CategoryResponse;
import com.financemanager.dto.CreateCategoryRequest;
import com.financemanager.exception.ForbiddenException;
import com.financemanager.entity.User;
import com.financemanager.service.AuthenticationService;
import com.financemanager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("password")
                .fullName("Test User")
                .phoneNumber("+10000000000")
                .build();
        given(authenticationService.getCurrentUser()).willReturn(user);
    }

    @Test
    void getAllCategories_returnsOk() throws Exception {
        CategoryResponse c1 = CategoryResponse.builder().name("Salary").type("INCOME").isCustom(false).build();
        CategoryResponse c2 = CategoryResponse.builder().name("Food").type("EXPENSE").isCustom(false).build();
        given(categoryService.getAllCategories(user)).willReturn(CategoriesResponse.builder().categories(List.of(c1, c2)).build());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(2));
    }

    @Test
    void createCategory_returnsCreated() throws Exception {
        CreateCategoryRequest req = CreateCategoryRequest.builder().name("SideBusinessIncome").type("INCOME").build();
        CategoryResponse resp = CategoryResponse.builder().name("SideBusinessIncome").type("INCOME").isCustom(true).build();
        given(categoryService.createCustomCategory(any(CreateCategoryRequest.class), eq(user))).willReturn(resp);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SideBusinessIncome"))
                .andExpect(jsonPath("$.isCustom").value(true));
    }

    @Test
    void deleteCategory_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/categories/SideBusinessIncome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void deleteCategory_default_forbidden() throws Exception {
        // Simulate service preventing deletion of default category
        org.mockito.BDDMockito.willThrow(new ForbiddenException("Cannot delete default category"))
                .given(categoryService).deleteCustomCategory(eq("Salary"), eq(user));

        mockMvc.perform(delete("/api/categories/Salary"))
                .andExpect(status().isForbidden());
    }
}
