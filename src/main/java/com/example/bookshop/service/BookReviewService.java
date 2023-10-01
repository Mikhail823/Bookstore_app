package com.example.bookshop.service;

import com.example.bookshop.repository.BookReviewRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookReviewService {
    @Autowired
    private final BookReviewRepository bookReviewRepository;
    @Autowired
    private final BookService bookService;

    public void saveReviewText(String slug, String textReview){
        BookEntity book = bookService.getBookPageSlug(slug);
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                BookReviewEntity review = new BookReviewEntity();
                review.setUserId(userDetails.getContact().getUserId());
                review.setBookId(book);
                review.setTime(new Date());
                review.setText(textReview);
                review.setRating(0);
                bookReviewRepository.save(review);
    }
}
