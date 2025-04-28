package com.focussu.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

        // Bearer 토큰을 위한 스키마 설정
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 보안 요구사항 추가 (모든 API 엔드포인트에 적용되는 경우)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .servers(List.of(localServer, prodServer))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerScheme))
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Focussu Backend API")
                .description("2025-1 캡스톤2")
                .version("1.0.0");
    }
}
