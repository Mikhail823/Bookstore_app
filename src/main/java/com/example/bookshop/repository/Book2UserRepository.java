package com.example.bookshop.repository;

import com.example.bookshop.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

   Book2UserEntity findBook2UserEntityByUserIdAndBookId(Integer user, Integer book);

   List<Book2UserEntity> findBook2UserEntityByUserId(Integer userId);

   Book2UserEntity findFirstByUserIdAndBookId(Integer user, Integer book);

   Book2UserEntity findBook2UserEntityByBookId(Integer bookId);

   int countBook2UserEntityByUserIdAndTypeId(Integer userId, Integer typeId);

   @Query(value = "SELECT count(bu) FROM Book2UserEntity AS bu WHERE bu.userId=:user_id AND bu.typeId=:type_id")
   Integer getCountBooksCart(@Param("user_id") Integer userId, @Param("type_id") Integer typeId);

   @Modifying
   @Query(value = "UPDATE Book2UserEntity AS bu SET bu.typeId=:type WHERE bu.userId=:userId AND bu.bookId=:bookId")
   void updateBookUser(@Param("userId") Integer userId, @Param("bookId") Integer bookId, @Param("type") Integer type);
}
