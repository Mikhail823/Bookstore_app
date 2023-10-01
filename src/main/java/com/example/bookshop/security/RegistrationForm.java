package com.example.bookshop.security;

import lombok.Data;

@Data
public class RegistrationForm {

    private String name;
    private String phone;
    private String email;
    private String pass;
}
