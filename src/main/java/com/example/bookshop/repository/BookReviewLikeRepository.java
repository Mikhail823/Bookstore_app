package com.example.bookshop.repository;

import com.example.bookshop.struct.book.review.BookReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
    @Query(value = "select count(value) from book_review_like where value = -1 and review_id = :id", nativeQuery = true)
    Long countLike(@Param("id") Integer id);

    @Query(value = "select count(value) from book_review_like where value = -1 and review_id = ?1", nativeQuery = true)
    Long countDisLike(Integer id);


}
