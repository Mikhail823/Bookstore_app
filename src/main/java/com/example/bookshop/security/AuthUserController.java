package com.example.bookshop.security;

import com.example.bookshop.security.exception.UserNotFoundException;

import com.example.bookshop.service.InspectorService;
import com.example.bookshop.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthUserController {

    private final BookstoreUserRegister userRegister;
    private final SmsService smsService;
    private final InspectorService inspectorService;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister,
                              SmsService smsService,
                              InspectorService inspectorService) {
        this.userRegister = userRegister;
        this.smsService = smsService;
        this.inspectorService = inspectorService;
    }

    @GetMapping("/signin")
    public String handleSignin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {

        model.addAttribute("registerForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailContactConfirmation
            (@RequestBody ContactConfirmationPayload payload, HttpServletRequest request) {

        ContactConfirmationResponse response = new ContactConfirmationResponse();
        inspectorService.sendTheCodeToMailUser(payload, request);
        response.setResult("true");
        return response;
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation
            (@RequestBody ContactConfirmationPayload payload, HttpServletRequest request) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
            response.setResult("true");
        if (!payload.getContact().contains("@")){
            inspectorService.restApiRequestCodeSmsRu(payload, request);
        }
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        if(Boolean.TRUE.equals(smsService.verifyCode(payload.getCode()))){
            response.setResult("true");
            return response;
        }
        return new ContactConfirmationResponse();

    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model, HttpServletRequest request) {
        userRegister.registrationNewUser(registrationForm, request);
        model.addAttribute("regOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse){

        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
                Cookie cookie = new Cookie("token", loginResponse.getResult());
                httpServletResponse.addCookie(cookie);
                return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByNumber(@RequestBody ContactConfirmationPayload payload,
                                                           HttpServletResponse response) {

        if (Boolean.TRUE.equals(smsService.verifyCode(payload.getCode()))){
            ContactConfirmationResponse loginNumberResponse = userRegister.jwtLoginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginNumberResponse.getResult());
            response.addCookie(cookie);
            return loginNumberResponse;
        }
        else {
            throw new UserNotFoundException("Пользователь не зарегистрирован");
        }
    }
}
