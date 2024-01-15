package com.example.bookshop.exeption;

public class InvalidPasswordException extends Exception{

    public InvalidPasswordException(String msg){
        super(msg);
    }
}
