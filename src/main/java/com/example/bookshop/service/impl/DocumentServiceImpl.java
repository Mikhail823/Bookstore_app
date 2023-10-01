package com.example.bookshop.service.impl;

import com.example.bookshop.repository.DocumentRepository;
import com.example.bookshop.service.DocumentService;
import com.example.bookshop.struct.other.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private final DocumentRepository documentRepository;

    @Override
    @Cacheable(value="document", key = "#slug")
    public DocumentEntity findDocumentBySlug(String slug) {
        return documentRepository.findDocumentEntityBySlug(slug);
    }

    @Override

    public List<DocumentEntity> getAllDocument() {
        return documentRepository.findAll();
    }


}
