package com.lecfantasy.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentación de la API en /swagger-ui.html (útil para la memoria del TFG). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lecFantasyOpenApi() {
        return new OpenAPI()
                .info(new Info().title("LEC Fantasy API").version("1.0")
                        .description("API REST compartida por la web (Vue) y la app Android (Kotlin)."))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
