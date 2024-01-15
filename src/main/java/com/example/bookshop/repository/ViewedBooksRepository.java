package com.example.bookshop.repository;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.ViewedBooks;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewedBooksRepository extends JpaRepository<ViewedBooks, Integer> {
    ViewedBooks findFirstByBookAndUser(BookEntity book, UserEntity user);

    Page<ViewedBooks> findViewedBooksByAndUser(UserEntity user, Pageable pageable);

    List<ViewedBooks> findViewedBooksByUser(UserEntity user);

}
