package com.example.bookshop.struct.book.review;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "book_review")
@Setter
@Getter
@EqualsAndHashCode
public class BookReviewEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity bookId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private Date time;

    @Column(length = 1000, columnDefinition = "TEXT NOT NULL")
    private String text;
    @Column(name = "rating", columnDefinition = "int default 0")
    private Integer rating;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reviewId", fetch = FetchType.EAGER)
    private Set<BookReviewLikeEntity> likeList = new HashSet<>();

    public long getLikeCount(){
        return getLikeList().stream().filter(like -> like.getValue() == 1).count();
    }

    public long getDisLikeCount(){
        return getLikeList().stream().filter(disLike -> disLike.getValue() == -1).count();
    }
}
