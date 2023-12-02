package com.example.bookshop.dto;

import com.example.bookshop.struct.book.author.AuthorEntity;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.tags.TagEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class BookDto {
    private String title;
    private String description;
    private String bestseller;
    private String pubDate;
    private String price;
    private String discountPrice;
    private List<AuthorEntity> authors;
    private GenreEntity genre;
    private List<TagEntity> tags;
    private String image;
}
