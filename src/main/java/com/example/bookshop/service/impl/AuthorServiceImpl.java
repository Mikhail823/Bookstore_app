package com.example.bookshop.service.impl;

import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.struct.book.author.AuthorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private final AuthorRepository authorRepository;

    @Cacheable(value="authorsMap")
    @Override
    public Map<String, List<AuthorEntity>> getAuthorsMap(){
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((AuthorEntity a)->{return a.getLastName().substring(0,1);}));
    }

    @Override
    public AuthorEntity getAuthorById(Integer id){
        return authorRepository.findAuthorEntityById(id);
    }
}
