package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.TagRepository;
import com.example.bookshop.service.TagService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.tags.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final BookRepository bookRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, BookRepository bookRepository) {
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<TagEntity> getTags(){
        return tagRepository.findAll();
    }

    @Override
    public TagEntity gatOneTag(Integer id){
        return tagRepository.getOne(id);
    }

    @Override
    public Page<BookEntity> getBooksOfTags(Integer tagId, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getBookEntitiesByTagId(tagId, nextPage);
    }
}
