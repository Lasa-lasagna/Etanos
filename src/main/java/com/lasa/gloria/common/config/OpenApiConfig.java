package com.lasa.gloria.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gloriaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Gloria Inventory API")
                .description("API de inventario (entradas, salidas, ajustes, kardex, valoración).")
                .version("v1")
                .contact(new Contact().name("Gloria").email("dev@gloria.local")));
    }
}
