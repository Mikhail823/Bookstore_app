package com.example.bookshop.struct.genre;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.enums.GenreType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genres")
@Setter
@Getter
public class GenreEntity implements Serializable {

    private static final Long serialVersionUID  =  2405172041950251807L;
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

    @OneToMany
    @JoinTable(name = "book2genre",
            joinColumns = {@JoinColumn(name = "genre_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    @JsonIgnore
    private List<BookEntity> listBook = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
