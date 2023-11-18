package com.example.bookshop.dto;

import lombok.Data;

@Data
public class AuthorDto {
    private String firstName;
    private String lastName;
    private String photo;
    private String description;
}
