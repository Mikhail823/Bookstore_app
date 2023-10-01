package com.example.bookshop.sessions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class SecurityErrorHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        if (!exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
            super.onAuthenticationFailure(request, response, exception);
        }
        log.debug("onAuthenticationFailure#set multiple choices for response");
        response.setStatus(HttpStatus.MULTIPLE_CHOICES.value());
    }
}