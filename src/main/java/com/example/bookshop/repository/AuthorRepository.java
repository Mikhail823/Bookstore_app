package com.example.bookshop.repository;


import com.example.bookshop.struct.book.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    AuthorEntity findAuthorEntityById(Integer id);

    AuthorEntity findAuthorEntityByLastName(String lastName);
}
