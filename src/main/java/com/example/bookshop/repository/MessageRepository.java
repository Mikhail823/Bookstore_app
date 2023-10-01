package com.example.bookshop.repository;

import com.example.bookshop.struct.book.review.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
