package com.example.bookshop.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    public DateFormatter(){}

    public static Date getToDateFormat(String date){
        try{
            return DATE_FORMAT.parse(date);
        } catch (ParseException e){
            return new Date();
        }

    }
}
