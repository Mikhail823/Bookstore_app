package com.example.bookshop.repository;

import com.example.bookshop.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

   Book2UserEntity findBook2UserEntityByUserIdAndBookId(Integer user, Integer book);
//   @Query(value = "select b2u from book2user b2u where b2u.book_id = :book and b2u.user_id= :user", nativeQuery = true)
//   Book2UserEntity findBook2UserEntityByBookIdAndUserIddd(@Param("book") BookEntity book, @Param("user") UserEntity user);
//
//   Book2UserEntity getBook2UserEntityByBookIdAndBookId(Integer book, Integer user);

}
