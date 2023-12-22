package com.example.bookshop.service.impl;

import com.example.bookshop.repository.ViewedBooksRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.ViewedBooksService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import com.example.bookshop.struct.book.links.ViewedBooks;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class ViewedBooksServiceImpl implements ViewedBooksService {

    private final ViewedBooksRepository viewedBooksRepository;
    private final BookstoreUserRegister registerUser;

    @Autowired
    public ViewedBooksServiceImpl(ViewedBooksRepository viewedBooksRepository, BookstoreUserRegister registerUser) {
        this.viewedBooksRepository = viewedBooksRepository;
        this.registerUser = registerUser;
    }

    @Override
    public void saveViewedBooksUser(BookEntity book, HttpServletRequest request){
        ViewedBooks viewedBooks = new ViewedBooks();
        Object user = registerUser.getCurrentUser();

            if (registerUser.isAuthUser()) {
                if (getViewedBookUser(book, ((BookstoreUserDetails) user).getContact().getUserId()) != null) return;
                viewedBooks.setBook(book);
                viewedBooks.setUser(((BookstoreUserDetails) user).getContact().getUserId());
                viewedBooks.setType(Book2UserTypeEntity.StatusBookType.VIEWED);
                viewedBooks.setTime(LocalDateTime.now());
                viewedBooksRepository.save(viewedBooks);
            }
    }

    public ViewedBooks getViewedBookUser(BookEntity book, UserEntity user){
        return viewedBooksRepository.findFirstByBookAndUser(book, user);
    }
}
