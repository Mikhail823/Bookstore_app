package com.example.bookshop.service.impl;

import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.service.SmsService;
import com.example.bookshop.struct.user.UserContactEntity;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${twilio.ACCOUNT_SID}")
    private String AUTH_TOKEN;

    @Value("${twilio.ACCOUNT_SID}")
    private String TWILIO_NUMBER;
    @Autowired
    private final UserContactRepository contactRepository;

    @Override
    public String sendSecretCodeSms(String contact) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String formattedContact = contact.replaceAll("[()-]]", "");
        String generatedCode = generateCode();
        Message.creator(
                new PhoneNumber(formattedContact),
                new PhoneNumber(TWILIO_NUMBER),
                "Your secret code is: " + generatedCode
        ).create();
        return generatedCode;
    }

    @Override
    public String generateCode() {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 6) {
            sb.append(random.nextInt(9));
        }
        sb.insert(3, " ");
        return sb.toString();
    }

    @Override
    public void saveNewCode(UserContactEntity contact) {
        if (contactRepository.findUserContactEntityByCode(contact.getCode()) == null) {
            contactRepository.save(contact);
        }
    }

    @Override
    public Boolean verifyCode(String code) {
        UserContactEntity contact = contactRepository.findUserContactEntityByCode(code);
        return (contact != null);
    }

}
