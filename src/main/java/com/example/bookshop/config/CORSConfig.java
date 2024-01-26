package com.example.bookshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc
public class CORSConfig implements WebMvcConfigurer{


//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/profile")
//                .allowedOrigins("http://192.168.1.3:8081")
//                .allowCredentials(true)
//                .allowedHeaders("Access-Control-Allow-Origin: http://192.168.1.3:8081")
//                .allowedMethods("*")
//                .maxAge(3600);
//    }
}
