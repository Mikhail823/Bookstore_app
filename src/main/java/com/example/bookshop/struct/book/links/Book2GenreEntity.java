package com.example.bookshop.struct.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "book2genre")
@Setter
@Getter
public class Book2GenreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name = "genre_id", columnDefinition = "INT NOT NULL")
    private Integer genreId;
}
