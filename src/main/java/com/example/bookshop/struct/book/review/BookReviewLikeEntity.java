package com.example.bookshop.struct.book.review;
import com.example.bookshop.struct.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "book_review_like")
@Setter
@Getter
public class BookReviewLikeEntity  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private BookReviewEntity reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private Date time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private short value;
}
