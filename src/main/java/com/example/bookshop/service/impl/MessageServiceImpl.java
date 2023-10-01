package com.example.bookshop.service.impl;

import com.example.bookshop.dto.MessageFormDto;
import com.example.bookshop.repository.MessageRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.MessageService;
import com.example.bookshop.struct.book.review.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    @Value("${email.address.shop}")
    private String email;

    @Autowired
    private final JavaMailSender javaMailSender;

    @Autowired
    private final MessageRepository messageRepository;
    @Autowired
    private final BookstoreUserRegister userRegister;

    @Override
    public void saveMessage(MessageFormDto messageFormDto){
        MessageEntity message = new MessageEntity();
        Object user = userRegister.getCurrentUser();
        if (user instanceof BookstoreUserDetails){
            message.setUserId(((BookstoreUserDetails) user).getContact().getUserId());
            message.setEmail(messageFormDto.getMail());
            message.setName(messageFormDto.getName());
            message.setSubject(messageFormDto.getTopic());
            message.setText(messageFormDto.getMessage());
            message.setTime(LocalDateTime.now());
            sendingMessages(messageFormDto);
            messageRepository.save(message);
        }
        message.setEmail(messageFormDto.getMail());
        message.setName(messageFormDto.getName());
        message.setSubject(messageFormDto.getTopic());
        message.setText(messageFormDto.getMessage());
        message.setTime(LocalDateTime.now());
        sendingMessages(messageFormDto);
        messageRepository.save(message);
    }

    public void sendingMessages(MessageFormDto formDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(email);
        message.setSubject(formDto.getTopic());
        message.setText(formDto.getMessage() + "\n\n" + formDto.getName() + ",\n" + formDto.getMail());
        message.setReplyTo(formDto.getMail());
        javaMailSender.send(message);
    }
}
