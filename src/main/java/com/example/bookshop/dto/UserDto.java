package com.example.bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor

public class UserDto {
    private String name;
    private String phone;
    private String email;
    private String password;
    private Integer balance;
    private Date reqTime;
    private String hash;
}
