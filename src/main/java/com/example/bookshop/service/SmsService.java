package com.example.bookshop.service;

import com.example.bookshop.struct.user.UserContactEntity;

import org.springframework.stereotype.Service;


@Service
public interface SmsService {
    String sendSecretCodeSms(String contact);
    String generateCode();
    void saveNewCode(UserContactEntity contact);
    Boolean verifyCode(String code);
}
