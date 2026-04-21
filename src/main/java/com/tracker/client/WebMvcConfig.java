package com.tracker.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registers the UserInterceptor for all API requests (Change 3). */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserInterceptor userInterceptor;

    public WebMvcConfig(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).addPathPatterns("/api/**");
    }
}
