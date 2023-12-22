package com.example.bookshop.service.impl;

import com.example.bookshop.dto.AuthorDto;
import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.struct.book.author.AuthorEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Map<String, List<AuthorEntity>> getAuthorsMap(){
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy((AuthorEntity a)-> a.getLastName().substring(0,1)));
    }

    @Override
    public AuthorEntity getAuthorById(Integer id){
        return authorRepository.findAuthorEntityById(id);
    }


    @Override
    @Transient
    public void saveNewAuthor(AuthorDto authorDto){
        AuthorEntity newAuthor = authorRepository.findAuthorEntityByLastName(authorDto.getLastName());
        if (newAuthor == null) {
            AuthorEntity a = new AuthorEntity();
            a.setFirstName(authorDto.getFirstName());
            a.setLastName(authorDto.getLastName());
            a.setPhoto(authorDto.getPhoto());
            a.setSlug(generateSlug());
            a.setDescription(authorDto.getDescription());
            authorRepository.save(a);
        }
    }

    public String generateSlug() {
        return RandomStringUtils.random(6, true, true);
    }

    @Override
    public List<AuthorEntity> getAllAuthors(){
        return authorRepository.findAll();
    }
}
