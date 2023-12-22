package com.example.bookshop.service.impl;

import com.example.bookshop.repository.DocumentRepository;
import com.example.bookshop.service.DocumentService;
import com.example.bookshop.struct.other.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public DocumentEntity findDocumentBySlug(String slug) {
        return documentRepository.findDocumentEntityBySlug(slug);
    }

    @Override

    public List<DocumentEntity> getAllDocument() {
        return documentRepository.findAll();
    }


}
