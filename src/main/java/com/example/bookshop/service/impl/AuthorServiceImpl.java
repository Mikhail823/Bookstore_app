package com.example.bookshop.service.impl;

import com.example.bookshop.dto.AuthorDto;
import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.struct.book.author.AuthorEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
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
