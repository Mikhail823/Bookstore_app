package com.example.bookshop.struct.book.ratings;

import com.example.bookshop.struct.book.BookEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rating_book")
@Getter
@Setter
public class RatingBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity book;
    @Column(name = "one_star")
    private Integer oneStar;
    @Column(name = "two_star")
    private Integer twoStar;
    @Column(name = "three_star")
    private Integer threeStar;
    @Column(name = "four_star")
    private Integer fourStar;
    @Column(name = "five_star")
    private Integer fiveStar;

}
