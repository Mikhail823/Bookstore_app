package com.example.bookshop.dto;

import lombok.Data;

@Data
public class RatingRequestDto {
    private Integer bookId;
    private Integer value;
}
