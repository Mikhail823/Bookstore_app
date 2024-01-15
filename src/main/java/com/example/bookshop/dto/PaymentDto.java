package com.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class PaymentDto {

    private String hash;
    private String sum;
    private Date time;
}
