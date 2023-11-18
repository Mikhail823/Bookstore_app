package com.example.bookshop.service;

import com.example.bookshop.dto.AuthorDto;
import com.example.bookshop.struct.book.author.AuthorEntity;
import java.util.List;
import java.util.Map;


public interface AuthorService {

    Map<String, List<AuthorEntity>> getAuthorsMap();

    AuthorEntity getAuthorById(Integer id);

    void saveNewAuthor(AuthorDto authorDto);
}
