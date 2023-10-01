package com.example.bookshop.dto;

import lombok.Data;

@Data
public class ResponseBodyCode {

    private String status;
    private Integer code;
    private String callId;
}
