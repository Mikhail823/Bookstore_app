package com.example.bookshop.service.impl;

import com.example.bookshop.repository.Book2UserRepository;
import com.example.bookshop.repository.Book2UserTypeRepository;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.ViewedBooksService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Service
@Slf4j
public class ViewedBooksServiceImpl implements ViewedBooksService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final BookstoreUserRegister userRegister;


    @Autowired
    public ViewedBooksServiceImpl(BookRepository bookRepository,
                                  Book2UserRepository book2UserRepository,
                                  Book2UserTypeRepository book2UserTypeRepository,
                                  BookstoreUserRegister userRegister) {
        this.bookRepository = bookRepository;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
        this.userRegister = userRegister;

    }

    @Override
    @Transactional
    public void saveViewedBooksUser(BookEntity book, HttpServletRequest request) {
        Object user = userRegister.getCurrentUser();
        Book2UserEntity newBookUser = null;

        while (user instanceof BookstoreUserDetails){
            Book2UserEntity userBook =
                    book2UserRepository
                            .findBook2UserEntityByUserIdAndBookId(((BookstoreUserDetails) user).getContact()
                                    .getUserId().getId(), book.getId());

            if (userBook != null && book.getStatus().equals(Book2UserTypeEntity.StatusBookType.VIEWED)){
                return;
            }
            else {
                Book2UserTypeEntity book2UserType = book2UserTypeRepository
                        .findByCode(Book2UserTypeEntity.StatusBookType.VIEWED);
                book.setStatus(Book2UserTypeEntity.StatusBookType.VIEWED);
                bookRepository.save(book);
                newBookUser = new Book2UserEntity();
                newBookUser.setUserId(((BookstoreUserDetails) user).getContact().getUserId().getId());
                newBookUser.setBookId(book.getId());
                newBookUser.setTypeId(book2UserType.getId());
                newBookUser.setTime(new Date());
                book2UserRepository.save(newBookUser);
            }
        }
    }
}
