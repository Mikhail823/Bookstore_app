package com.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDto {

    private Integer offset;
    private Integer limit;
    private String sort;
}
