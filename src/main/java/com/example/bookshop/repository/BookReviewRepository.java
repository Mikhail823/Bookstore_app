package com.example.bookshop.repository;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

    List<BookReviewEntity> findBookReviewEntitiesByBookId(BookEntity id, Pageable limit);
    BookReviewEntity findBookReviewEntityById(Integer id);
    List<BookReviewEntity> findBookReviewEntitiesByUserId(Integer id);
    BookReviewEntity findBookReviewEntityByText(String text);
}
