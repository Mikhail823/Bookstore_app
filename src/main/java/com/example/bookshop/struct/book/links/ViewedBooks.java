package com.example.bookshop.struct.book.links;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "viewed_books")
@Setter
@Getter
public class ViewedBooks  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    private Book2UserTypeEntity.StatusBookType type;

    private LocalDateTime time;

}
