package com.financemanager.config;

import com.financemanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialization configuration for the application.
 */
@Configuration
@RequiredArgsConstructor
public class InitializationConfig {

    /**
     * Initialize default categories when the application starts.
     *
     * @param categoryService Category service
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner initializeCategories(CategoryService categoryService) {
        return args -> {
            try {
                categoryService.initializeDefaultCategories();
            } catch (Exception e) {
            }
        };
    }
}
