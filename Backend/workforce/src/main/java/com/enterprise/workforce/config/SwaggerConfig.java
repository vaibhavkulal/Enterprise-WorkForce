package com.enterprise.workforce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = new ArrayList<>();

        switch (activeProfile.toLowerCase()) {
            case "prod" -> servers.add(new Server()
                    .url("https://api.enterprise.com")
                    .description("Production Server (Port 8082)"));
            case "staging" -> servers.add(new Server()
                    .url("https://staging.enterprise.com")
                    .description("Staging/QA Server (Port 8081)"));
            default -> servers.add(new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Workforce & Access Management System")
                        .version("1.0")
                        .description("Comprehensive API documentation for Enterprise Workforce & Access Management System.\n\n**Active Environment:** `" + activeProfile.toUpperCase() + "`")
                        .contact(new Contact()
                                .name("Vaibhav")
                                .url("https://www.linkedin.com/in/vaibhavprofile2001/")
                                .email("vaibhav.kulal@amiti.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
