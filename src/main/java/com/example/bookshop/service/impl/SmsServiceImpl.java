package com.example.bookshop.service.impl;

import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.service.SmsService;
import com.example.bookshop.struct.user.UserContactEntity;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.ACCOUNT_SID}")
    private String accountSid;

    @Value("${twilio.ACCOUNT_SID}")
    private String authToken;

    @Value("${twilio.ACCOUNT_SID}")
    private String twilioNumber;

    private static Random random = new Random();

    private final UserContactRepository contactRepository;

    @Autowired
    public SmsServiceImpl(UserContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public String sendSecretCodeSms(String contact) {
        Twilio.init(accountSid, authToken);
        String formattedContact = contact.replaceAll("[()-]]", "");
        String generatedCode = generateCode();
        Message.creator(
                new PhoneNumber(formattedContact),
                new PhoneNumber(twilioNumber),
                "Your secret code is: " + generatedCode
        ).create();
        return generatedCode;
    }

    @Override
    public String generateCode() {

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
