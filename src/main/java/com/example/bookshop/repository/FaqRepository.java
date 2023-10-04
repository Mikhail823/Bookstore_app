package com.example.bookshop.repository;

import com.example.bookshop.struct.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntity, Integer> {
}
