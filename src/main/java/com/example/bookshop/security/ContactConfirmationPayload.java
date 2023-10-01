package com.example.bookshop.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContactConfirmationPayload {
    private String contact;
    private String code;
}
