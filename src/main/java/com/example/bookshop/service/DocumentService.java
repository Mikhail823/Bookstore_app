package com.example.bookshop.service;

import com.example.bookshop.struct.other.DocumentEntity;

import java.util.List;
import java.util.Map;

public interface DocumentService {

    DocumentEntity findDocumentBySlug(String slug);

    List<DocumentEntity> getAllDocument();

 //   Map<String, String> getContent();
}
