package com.example.bookshop.controllers;

import com.example.bookshop.exeption.CodeVerificationException;
import com.example.bookshop.exeption.EmptySearchException;
import com.example.bookshop.exeption.InvalidPasswordException;
import com.example.bookshop.security.exception.JWTAuthException;
import com.example.bookshop.security.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    public static final String REDIRECT_SIGNIN = "redirect:/signin";
    public static final String REDIRECT_SIGNUP = "redirect:/signup";
    public static final String REDIRECT_PROFILE = "redirect:/profile";

    @ExceptionHandler(EmptySearchException.class)
    public String emptySearchHandlerException(EmptySearchException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }

    @ExceptionHandler(JWTAuthException.class)
    public String securityLoginHandlerException(JWTAuthException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("loginError", e.getLocalizedMessage());
        log.info(e.getLocalizedMessage());
        return REDIRECT_SIGNUP;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String userNameException(UserNotFoundException us, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("userError", us.getLocalizedMessage());
        return REDIRECT_SIGNIN;
    }

    @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException jwtException) {
        log.info(jwtException.getLocalizedMessage());
        return REDIRECT_SIGNIN;
    }

    @ExceptionHandler(CodeVerificationException.class)
    public String handlerCodeException(CodeVerificationException e, RedirectAttributes redirect){
        redirect.addAttribute("codeError", e);
        return REDIRECT_SIGNIN;
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public String handlerPassError(InvalidPasswordException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("passError", e);
        log.error(e.getLocalizedMessage());
        return REDIRECT_PROFILE;
    }
}