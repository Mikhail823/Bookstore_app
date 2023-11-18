package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.User2RoleRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.service.AdminService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.example.bookshop.struct.user.links.User2Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final User2RoleRepository user2RoleRepository;
    @Autowired
    private final BookRepository bookRepository;

    @Override
    public List<UserEntity> getAllUsersShop() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(UserEntity user) {
        User2Role role = user2RoleRepository.findFirstByUser_id(user.getId());
        user2RoleRepository.delete(role);
        userRepository.delete(user);
    }

    @Override
    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }
}
