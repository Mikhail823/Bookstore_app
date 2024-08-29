package com.example.bookshop.service.impl;

import com.example.bookshop.repository.*;
import com.example.bookshop.service.AdminService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserContactEntity;
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
    private final UserContactRepository userContactRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            User2RoleRepository user2RoleRepository,
                            BookRepository bookRepository, UserContactRepository userContactRepository, BalanceTransactionRepository balanceTransactionRepository) {
        this.userRepository = userRepository;
        this.user2RoleRepository = user2RoleRepository;
        this.bookRepository = bookRepository;
        this.userContactRepository = userContactRepository;
        this.balanceTransactionRepository = balanceTransactionRepository;
    }

    @Override
    public List<UserEntity> getAllUsersShop() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(UserEntity user) {
        User2Role role = user2RoleRepository.findFirstByUser_id(user.getId());
        List<BalanceTransactionEntity> transactionList = balanceTransactionRepository.findBalanceTransactionEntitiesByUserId(user);
        balanceTransactionRepository.deleteAll(transactionList);
        List<UserContactEntity> contactsUser = userContactRepository.findUserContactEntitiesByUserId(user);
        userContactRepository.deleteAll(contactsUser);
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
