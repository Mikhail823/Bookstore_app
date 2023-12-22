package com.example.bookshop.repository;

import com.example.bookshop.struct.other.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {

    DocumentEntity findDocumentEntityBySlug(String slug);


}
