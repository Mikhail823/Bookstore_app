package com.example.bookshop.service;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.tags.TagEntity;
import org.springframework.data.domain.Page;


import java.util.List;


public interface TagService {

    List<TagEntity> getTags();

    TagEntity gatOneTag(Integer id);

    Page<BookEntity> getBooksOfTags(Integer tagId, Integer offset, Integer limit);

}
