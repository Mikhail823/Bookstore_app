package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.ViewedBooksRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.UserService;
import com.example.bookshop.service.ViewedBooksService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import com.example.bookshop.struct.book.links.ViewedBooks;
import com.example.bookshop.struct.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ViewedBooksServiceImpl implements ViewedBooksService {

    private final ViewedBooksRepository viewedBooksRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookstoreUserRegister userRegister;
    private final CookieService cookieService;


    @Autowired
    public ViewedBooksServiceImpl(ViewedBooksRepository viewedBooksRepository,
                                  BookRepository bookRepository, UserService userService,
                                  BookstoreUserRegister userRegister, CookieService cookieService) {
        this.viewedBooksRepository = viewedBooksRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.userRegister = userRegister;
        this.cookieService = cookieService;
    }

    @Override
    @Transactional
    public void saveViewedBooksUser(BookEntity book, HttpServletRequest request){
        ViewedBooks viewedBooks = new ViewedBooks();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if(userRegister.isAuthAnonymousUser()){
             UserEntity anonyUser = userService.findByUserFromHash(cookieService.getHashTheUserFromCookie(request));
             if (getViewedBookUser(book, userService.getUserName(auth.getName())) != null) return;
             viewedBooks.setBook(book);
             viewedBooks.setUser(anonyUser);
             viewedBooks.setType(Book2UserTypeEntity.StatusBookType.VIEWED);
             viewedBooks.setTime(LocalDateTime.now());
             viewedBooksRepository.save(viewedBooks);
             book.setStatus(Book2UserTypeEntity.StatusBookType.VIEWED);
             bookRepository.save(book);
         } else {
             if (getViewedBookUser(book,
                     ((BookstoreUserDetails) auth.getPrincipal()).getContact().getUserId()) != null) return;
             viewedBooks.setBook(book);
             viewedBooks.setUser(((BookstoreUserDetails) auth.getPrincipal()).getContact().getUserId());
             viewedBooks.setType(Book2UserTypeEntity.StatusBookType.VIEWED);
             viewedBooks.setTime(LocalDateTime.now());
             viewedBooksRepository.save(viewedBooks);
             book.setStatus(Book2UserTypeEntity.StatusBookType.VIEWED);
             bookRepository.save(book);

         }
 }

    public ViewedBooks getViewedBookUser(BookEntity book, UserEntity user){
        return viewedBooksRepository.findFirstByBookAndUser(book, user);
    }
}
