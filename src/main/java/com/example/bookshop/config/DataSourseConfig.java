package com.example.bookshop.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DataSourseConfig {
//    @Bean
//    @ConfigurationProperties("spring.datasource")
//    public HikariDataSource dataSource() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
}