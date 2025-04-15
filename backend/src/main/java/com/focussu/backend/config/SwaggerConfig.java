package com.focussu.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local 서버");

        Server prodServer = new Server()
                .url("https://focussu-api.life")
                .description("개발 서버");

        return new OpenAPI()
                .servers(List.of(localServer, prodServer))
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Focussu Backend API")
                .description("2025-1 캡스톤2")
                .version("1.0.0");
    }
}