package com.example.bookshop.struct.book.author;


import com.example.bookshop.struct.book.BookEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "authors")
@Setter
@Getter
@EqualsAndHashCode
public class AuthorEntity implements Serializable {

    private  static  final Long  serialVersionUID  =  1L ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "author id generated by db", position = 1)
    @JsonProperty("id")
    private Integer id;

    private String photo;

    private String slug;

    @ApiModelProperty(value = "first name of author", example = "Bob", position = 2)
    private String firstName;

    @ApiModelProperty(value = "last name of author", example = "Blaskovits", position = 3)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany
    @JoinTable(name = "book2author",
            joinColumns = {@JoinColumn(name = "author_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    @JsonIgnore
    private List<BookEntity> books = new ArrayList<>();


    @Override
    @JsonProperty("fullName")
    public String toString() {
        return firstName + ' ' + lastName;
    }

}
