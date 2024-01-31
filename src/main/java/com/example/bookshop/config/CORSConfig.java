package com.example.bookshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/payment")
                .allowedOrigins("https://auth.robokassa.ru")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}
