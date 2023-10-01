package com.example.bookshop.service;

import com.example.bookshop.security.ContactConfirmationPayload;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public interface InspectorService {

    void sendTheCodeToMailUser(ContactConfirmationPayload payload, HttpServletRequest request);

    void restApiRequestCodeSmsRu(ContactConfirmationPayload payload, HttpServletRequest request);

    String createRandomIpAddress();
}
