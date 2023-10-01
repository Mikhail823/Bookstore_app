package com.example.bookshop.repository;

import com.example.bookshop.dto.RatingCountI;
import com.example.bookshop.struct.book.ratings.RatingBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<RatingBookEntity, Integer> {
    @Query(value = "SELECT total, ((five_star*5  + four_star*4 + three_star*3  + two_star*2 + one_star) / total) AS average " +
            "FROM rating_book  " +
            ", LATERAL(SELECT five_star + four_star + one_star + three_star + two_star) AS s1(total) WHERE book_id = :bookId limit 1 ;", nativeQuery = true)
    RatingCountI getTotalAndAvgStars(@Param("bookId") Integer bookId);
    RatingBookEntity findRatingBookEntityByBookId(Integer id);
}
