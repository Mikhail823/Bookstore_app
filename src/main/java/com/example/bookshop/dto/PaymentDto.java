package com.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PaymentDto {

    private Double sum;
    private Date time;
}