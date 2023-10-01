package com.example.bookshop.service;

import com.example.bookshop.struct.book.BookEntity;

import javax.servlet.http.HttpServletRequest;

public interface ViewedBooksService {
    void saveViewedBooksUser(BookEntity book, HttpServletRequest request);
}
