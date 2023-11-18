package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookReviewRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.service.BookReviewService;
import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    @Autowired
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    private final BookService bookService;

    @Override
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
    public void deleteReviewUser(Integer id) {
        BookReviewEntity bookReviewUser = bookReviewRepository.findBookReviewEntityById(id);
        bookReviewRepository.delete(bookReviewUser);
    }


}
