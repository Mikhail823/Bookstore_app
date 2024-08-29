package com.example.bookshop.service.impl;

import com.example.bookshop.repository.Book2UserRepository;
import com.example.bookshop.repository.Book2UserTypeRepository;
import com.example.bookshop.service.CartService;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CartServiceImpl implements CartService {
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    @Autowired
    public CartServiceImpl(Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    @Override
    public Integer countBooksCartUser(Integer userId) {
        Optional<Book2UserTypeEntity> bookUserType = book2UserTypeRepository.findById(2);
        return book2UserRepository.getCountBooksCart(userId, bookUserType.get().getId());
    }
}
