package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.User2RoleRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.service.AdminService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.example.bookshop.struct.user.links.User2Role;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final User2RoleRepository user2RoleRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            User2RoleRepository user2RoleRepository,
                            BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.user2RoleRepository = user2RoleRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<UserEntity> getAllUsersShop() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(UserEntity user) {
        User2Role role = user2RoleRepository.findFirstByUser_id(user.getId());
        user2RoleRepository.delete(role);
        userRepository.deleteUserEntityById(user.getId());
    }

    @Override
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteBook(BookEntity book){
        bookRepository.delete(book);
    }
}
