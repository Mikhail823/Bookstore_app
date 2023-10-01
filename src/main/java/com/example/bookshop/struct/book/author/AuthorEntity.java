package com.example.bookshop.struct.book.author;


import com.example.bookshop.struct.book.BookEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "authors")
@Setter
@Getter
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "author id generated by db", position = 1)
    private Integer id;

    private String photo;

    private String slug;

    @ApiModelProperty(value = "first name of author", example = "Bob", position = 2)
    private String firstName;

    @ApiModelProperty(value = "last name of author", example = "Blaskovits", position = 3)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<BookEntity> bookList = new ArrayList<>();

    @Override
    public String toString() {
        return firstName + ' ' + lastName;
    }

}
