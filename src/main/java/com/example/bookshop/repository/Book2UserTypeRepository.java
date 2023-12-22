package com.example.bookshop.repository;

import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {

     Book2UserTypeEntity findByCode(Book2UserTypeEntity.StatusBookType type);
     Book2UserTypeEntity findBook2UserTypeEntityByCode(Book2UserTypeEntity.StatusBookType type);


}
