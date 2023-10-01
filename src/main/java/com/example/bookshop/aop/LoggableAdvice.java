package com.example.bookshop.aop;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Aspect
@Slf4j
public class LoggableAdvice {

    @Pointcut(value = "@annotation(com.example.bookshop.aop.annotations.LoggableExceptionHandler)")
    public void logPointcut(){
    }

    @AfterThrowing(pointcut = "logPointcut()", throwing = "ex")
    public void afterThrowingAdvice(Exception ex) {

        if (ex instanceof AuthenticationException){
            log.error("Authentication exception is occurred: {}", ex.getLocalizedMessage());
        }
        else if (ex instanceof JwtException){
            log.error("Jwt exception is occurred: {}", ex.getLocalizedMessage());
        }
        else {
            log.error("Exception is occurred: {}" + ex.getLocalizedMessage());
        }
    }

    @Pointcut(value = "execution(* *Login*(..))")
    public void allLoginMethodsPointcut() {
    }

    @After("allLoginMethodsPointcut()")
    public void execAdviceForAllLoginMethods(JoinPoint joinPoint) {
        log.info("Attempt to login. {} was involved", joinPoint.getTarget().getClass().getSimpleName());
    }

    @Pointcut(value = "within(com.example.bookshop.controllers.BooksRestApiController)")
    public void allMethodsOfBooksRestApiControllerPointcut() {
    }

    @After("allMethodsOfBooksRestApiControllerPointcut()")
    public void allMethodsOfBooksRestApiControllerAdvice(JoinPoint joinPoint) {
        log.info("{} method of books rest api controller invoked", joinPoint);
    }


    @Pointcut(value = "@annotation(com.example.bookshop.aop.annotations.LogSQLException)")
    public void logSQLPointcut(){
    }

    @AfterThrowing(pointcut = "logSQLPointcut()", throwing = "ex")
    public void afterThrowingSQLAdvice(Exception ex) {

        if (ex instanceof SQLException){
            log.error("Error save book2user: {} ", ex.getLocalizedMessage());
        }
        else {
            log.error("Error runtime database: {}", ex.getLocalizedMessage());
        }
    }

    @Pointcut(value = "execution(* getBook*(..)))")
    public void allMethodBookServicePointcut(){
    }

    @After(value = "allMethodBookServicePointcut()")
    protected void afterMethodAdvice(JoinPoint joinPoint){
        log.info("Method execution: {}", joinPoint.getTarget().toString());
    }

    @Pointcut(value = "execution(* *Cookie(..)))")
    public void allMethodCookieComponentPointcut(){
    }

    @After(value = "allMethodCookieComponentPointcut()")
    protected void afterMethodCookieAdvice(JoinPoint joinPoint){
        log.info("Executing the method with Cookies: {}", joinPoint.getTarget().toString());
    }

}
