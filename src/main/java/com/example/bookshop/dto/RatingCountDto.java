package com.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingCountDto {

    private Integer total;

    private Integer average;

    public RatingCountDto(Integer total, Integer average) {
        this.total = total;
        this.average = average;
    }
}

