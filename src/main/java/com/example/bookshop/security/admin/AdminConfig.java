package com.example.bookshop.security.admin;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:admin.properties")
@Data
public class AdminConfig {
    @Value("${admin.login}")
    private String login;
    @Value("${admin.phone}")
    private  String phone;
    @Value("${admin.email}")
    private  String email;
    @Value("${admin.pass}")
    private String pass;

}
