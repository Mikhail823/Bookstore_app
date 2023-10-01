package com.example.bookshop.config;


import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageHandler implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        List<String> languages = new ArrayList<>();
        languages.add("en");
        languages.add("ru");

        if(!languages.contains(request.getParameter("lang"))
                && request.getParameter("lang") != null){
            sessionLocaleResolver.setLocale(request, response, new Locale("ru"));
        }
        return true;
    }
}
