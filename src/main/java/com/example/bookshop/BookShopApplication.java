package com.example.bookshop;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@AutoConfigurationPackage
@LiquibaseDataSource
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
public class BookShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

}
