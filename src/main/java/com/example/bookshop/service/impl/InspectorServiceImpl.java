package com.example.bookshop.service.impl;

import com.example.bookshop.dto.ResponseBodyCode;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.security.ContactConfirmationPayload;
import com.example.bookshop.service.InspectorService;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Component
public class InspectorServiceImpl implements InspectorService {

    private final SmsServiceImpl smsServiceImpl;
    private final UserServiceImp userServiceImp;
    private final BookstoreUserRegister userRegister;
    private final JavaMailSender javaMailSender;
    private final RestTemplate restTemplate;
    @Value("${sms.api_id}")
    private String apiId;

    private static  Random random = new Random();

    @Autowired
    public InspectorServiceImpl(SmsServiceImpl smsServiceImpl, UserServiceImp userServiceImp,
                                BookstoreUserRegister userRegister, JavaMailSender javaMailSender,
                                RestTemplate restTemplate) {
        this.smsServiceImpl = smsServiceImpl;
        this.userServiceImp = userServiceImp;
        this.userRegister = userRegister;
        this.javaMailSender = javaMailSender;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendTheCodeToMailUser(ContactConfirmationPayload payload, HttpServletRequest request){
         UserEntity user = userServiceImp.findByUserFromHash(userRegister.getHashOfTheUserFromCookie(request));

         SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
         simpleMailMessage.setFrom("rabota822@bk.ru");
         simpleMailMessage.setTo(payload.getContact());

         UserContactEntity contact = new UserContactEntity(smsServiceImpl.generateCode(), 400);
         contact.setUserId(user);
         contact.setType(ContactType.EMAIL);
         contact.setApproved((short) 0);
         contact.setContact(payload.getContact());
         smsServiceImpl.saveNewCode(contact);

         simpleMailMessage.setSubject("Bookstore email verification");
         simpleMailMessage.setText("Verification code is: " + contact.getCode());

         javaMailSender.send(simpleMailMessage);
     }

     @Override
     public void restApiRequestCodeSmsRu(ContactConfirmationPayload payload, HttpServletRequest request){
         UserContactEntity userContact = new UserContactEntity();
         HttpHeaders headers = new HttpHeaders();
         UserEntity userAny = userServiceImp.findByUserFromHash(userRegister.getHashOfTheUserFromCookie(request));

         String url = "https://sms.ru/code/call" +
                 "?phone=" + payload.getContact() +
                 "&ip="  + createRandomIpAddress() +
                 "&api_id=" + apiId;

         headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
         HttpEntity<String> entity = new HttpEntity<>(headers);
         ResponseEntity<ResponseBodyCode> code =
                 restTemplate.exchange(url, HttpMethod.GET, entity, ResponseBodyCode.class);

         StringBuilder sb = new StringBuilder();
         sb.append(code.getBody().getCode());
         sb.insert(2, " ");
         userContact.setUserId(userAny);
         userContact.setApproved((short) 0);
         userContact.setType(ContactType.PHONE);
         userContact.setContact(payload.getContact());
         userContact.setCode(sb.toString());
         userContact.setCodeTime(LocalDateTime.now());
         smsServiceImpl.saveNewCode(userContact);
     }


     @Override
     public String createRandomIpAddress(){

          return random.nextInt(255) + "." + random.nextInt(255)
                 + "." + random.nextInt(255) + "." + random.nextInt(255);
     }

}
