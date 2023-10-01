package com.example.bookshop.service;

import com.example.bookshop.dto.MessageFormDto;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
    void saveMessage(MessageFormDto messageFormDto);
}
