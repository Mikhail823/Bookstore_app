package com.example.bookshop.struct.genre;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.enums.GenreType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
@Setter
@Getter
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Enumerated(EnumType.ORDINAL)
    @Column(name = "parent_id")
    private GenreType parentId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<BookEntity> listBook = new ArrayList<>();


}
