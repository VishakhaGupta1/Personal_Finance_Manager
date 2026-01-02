package com.financemanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Personal Finance Manager API")
                        .version("1.0.0")
                        .description("REST API for Personal Finance Manager (transactions, categories, goals, reports)")
                        .contact(new Contact().name("Finance Manager Team").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}
