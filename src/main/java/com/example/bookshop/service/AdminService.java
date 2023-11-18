package com.example.bookshop.service;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;

import java.util.List;

public interface AdminService {

    List<UserEntity> getAllUsersShop();

    void deleteUser(UserEntity user);

    List<BookEntity> getAllBooks();

}
