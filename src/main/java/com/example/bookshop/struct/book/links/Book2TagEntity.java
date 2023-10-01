package com.example.bookshop.struct.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "book2tag")
@Setter
@Getter
public class Book2TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "tag_id", columnDefinition = "INT NOT NULL")
    private int tagId;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private int bookId;

}
