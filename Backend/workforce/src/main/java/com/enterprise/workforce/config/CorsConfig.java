package com.enterprise.workforce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        //  Allow your frontend origin
        config.setAllowedOrigins(List.of("http://localhost:3333"));

        //  Allow required methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //  Allow required headers
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        //  Allow credentials (important if you're sending cookies or Authorization headers)
        config.setAllowCredentials(true);

        //  Set max age (preflight cache time)
        config.setMaxAge(3600L);

        // Apply the config globally
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
