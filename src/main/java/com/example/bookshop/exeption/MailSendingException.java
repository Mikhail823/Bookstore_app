package com.example.bookshop.exeption;

public class MailSendingException extends Exception{

    public MailSendingException(String msg){
        super(msg);
    }
}
