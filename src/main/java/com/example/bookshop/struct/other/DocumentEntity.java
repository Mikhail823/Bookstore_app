package com.example.bookshop.struct.other;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "document")
@Setter
@Getter
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private int sortIndex;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String paragraph;

}
