package com.tracker.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the GitHub Pages frontend (different origin) to call the Render backend.
 * Without this, browsers block cross-origin fetch() requests.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "https://rohithgowdad.github.io",  // GitHub Pages frontend
                    "http://localhost:8080",            // local development
                    "http://localhost:3000"             // local dev alternative
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
