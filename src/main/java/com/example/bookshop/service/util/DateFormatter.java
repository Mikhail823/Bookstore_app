package com.example.bookshop.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public  Date getToDateFormat(String date){
        try{
            return dateFormat.parse(date);
        } catch (ParseException e){
            return new Date();
        }

    }
}
