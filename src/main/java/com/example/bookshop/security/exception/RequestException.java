package com.example.bookshop.security.exception;

public class RequestException extends Exception{

    private String msg;

    public RequestException(String msg){
        super(msg);
    }
}
