package com.example.bookshop.repository;

import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
     //    @Query(value = "SELECT * FROM book2user_type WHERE code = ?1", nativeQuery = true)
//    Book2UserTypeEntity findByCode(StatusBookType code);
//     @Query(value = "select * from book2user_type where code = ?1", nativeQuery = true)
//     Book2UserTypeEntity findByCodeType( StatusBookType type);
//     Book2UserTypeEntity findBook2UserTypeEntityByCode(StatusBookType type);
////    Book2UserTypeEntity findBook2UserTypeEntityByCode(String status);
     Book2UserTypeEntity findByCode(Book2UserTypeEntity.StatusBookType type);
     Book2UserTypeEntity findBook2UserTypeEntityByCode(Book2UserTypeEntity.StatusBookType type);


}
