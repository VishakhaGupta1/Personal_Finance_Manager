package com.financemanager.config;

import com.financemanager.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes default categories at application startup.
 */
@Configuration
public class DefaultDataInitializer {

    @Bean
    public CommandLineRunner initializeDefaults(CategoryService categoryService) {
        return args -> categoryService.initializeDefaultCategories();
    }
}
