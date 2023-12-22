package com.example.bookshop.controllers;

import com.example.bookshop.exeption.EmptySearchException;
import com.example.bookshop.security.exception.JWTAuthException;
import com.example.bookshop.security.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    public String emptySearchHandlerException(EmptySearchException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError", e);
        return "redirect:/";
    }



    @ExceptionHandler(JWTAuthException.class)
    public String securityLoginHandlerException(JWTAuthException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("loginError", e.getLocalizedMessage());
        log.info(e.getLocalizedMessage());
        return "redirect:/signup";
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNameException(UserNotFoundException us, RedirectAttributes redirectAttributes){
        redirectAttributes.addAttribute("userError", us);
        return "redirect:/signin";
    }

    @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException jwtException) {
        log.info(jwtException.getLocalizedMessage());
        return "redirect:/signin";
    }

}
