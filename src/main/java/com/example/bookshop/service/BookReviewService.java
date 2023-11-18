package com.example.bookshop.service;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;

public interface BookReviewService {

    void saveReviewText(String slug, String textReview);

    void deleteReviewUser(Integer id);
}
