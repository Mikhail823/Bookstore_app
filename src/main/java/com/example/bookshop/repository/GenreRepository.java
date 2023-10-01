package com.example.bookshop.repository;

import com.example.bookshop.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

}
