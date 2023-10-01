package com.example.bookshop.struct.tags;

import com.example.bookshop.struct.book.BookEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
@Setter
@Getter
public class TagEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "book2tag",
            joinColumns = @JoinColumn(name = "tag_id"), foreignKey = @ForeignKey(name = "fk_tag_book"),
            inverseJoinColumns = @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_book_tag"))
    )
    private List<BookEntity> books = new ArrayList<>();
}
