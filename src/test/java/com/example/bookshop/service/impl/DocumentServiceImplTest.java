package com.example.bookshop.service.impl;

import com.example.bookshop.repository.DocumentRepository;
import com.example.bookshop.service.DocumentService;
import com.example.bookshop.struct.other.DocumentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DisplayName("Проверка DocumentService")
class DocumentServiceImplTest {



    private final DocumentService documentService;

    @Autowired
    public DocumentServiceImplTest(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Test
    @DisplayName("Проверка получения документа по slug")
    void findDocumentBySlug() {
        DocumentEntity doc = documentService.findDocumentBySlug("document-059587");

        assertEquals("Публичная оферта", doc.getTitle());

        assertNotNull(doc);
    }

    @Test
    @DisplayName("Проверка получения всех документов")
    void getAllDocument() {
        List<DocumentEntity> documents = documentService.getAllDocument();

        assertNotNull(documents);
    }
}