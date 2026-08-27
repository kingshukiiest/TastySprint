package com.foodordering.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger UI configuration defining API metadata and global JWT Bearer Security Scheme.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Food Ordering System Backend API",
                version = "1.0",
                description = "Production-style Food Ordering System Modular Monolith Backend REST API Documentation",
                contact = @Contact(
                        name = "Backend Engineering",
                        email = "support@foodordering.com"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Provide the JWT Bearer token obtained from POST /api/auth/login or POST /api/auth/register"
)
public class SwaggerConfig {
}
