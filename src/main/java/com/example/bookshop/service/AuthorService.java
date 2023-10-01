package com.example.bookshop.service;


import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.struct.book.author.AuthorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    @Autowired
    private final AuthorRepository authorRepository;

    public Map<String, List<AuthorEntity>> getAuthorsMap(){
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((AuthorEntity a)->{return a.getLastName().substring(0,1);}));
    }

    public AuthorEntity getAuthorById(Integer id){
        return authorRepository.findAuthorEntityById(id);
    }




}
