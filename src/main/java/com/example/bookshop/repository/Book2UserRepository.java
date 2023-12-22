package com.example.bookshop.repository;

import com.example.bookshop.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

   Book2UserEntity findBook2UserEntityByUserIdAndBookId(Integer user, Integer book);
}
