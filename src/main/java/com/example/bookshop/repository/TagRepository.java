package com.example.bookshop.repository;

import com.example.bookshop.struct.tags.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    List<TagEntity> findAll();

}
