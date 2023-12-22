package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookReviewRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.service.BookReviewService;
import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookService bookService;

    @Autowired
    public BookReviewServiceImpl(BookReviewRepository bookReviewRepository, BookService bookService) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookService = bookService;
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void deleteReviewUser(Integer id) {
        BookReviewEntity bookReviewUser = bookReviewRepository.findBookReviewEntityById(id);
        bookReviewRepository.delete(bookReviewUser);
    }


}
