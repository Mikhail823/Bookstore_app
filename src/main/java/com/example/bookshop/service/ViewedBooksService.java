package com.example.bookshop.service;

import com.example.bookshop.struct.book.BookEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface ViewedBooksService {
    @Transactional
    void saveViewedBooksUser(BookEntity book, HttpServletRequest request);
}
