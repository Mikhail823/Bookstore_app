package com.example.bookshop.service;

public interface BookReviewService {

    void saveReviewText(String slug, String textReview);

    void deleteReviewUser(Integer id);
}
