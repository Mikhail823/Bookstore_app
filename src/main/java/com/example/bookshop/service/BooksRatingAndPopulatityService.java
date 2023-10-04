package com.example.bookshop.service;

import com.example.bookshop.dto.LikeReviewBookDto;
import com.example.bookshop.dto.RatingCountDto;
import com.example.bookshop.security.exception.RequestException;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.ratings.RatingBookEntity;
import com.example.bookshop.struct.book.review.BookReviewLikeEntity;

public interface BooksRatingAndPopulatityService {

    RatingCountDto getTotalAndAvgStars(Integer bookId);

    RatingBookEntity getRatingBook(Integer id);

    void ratingBookSave(Integer value, RatingBookEntity ratingBookEntity) throws RequestException;

    BookReviewLikeEntity saveLikeReviewBook(LikeReviewBookDto likeReviewBookDto);

    void saveRatingReview(Integer id);

   Object getCurrentUser();

    void calculatingThePopularityOfBook(BookEntity book);
}