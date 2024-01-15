package com.example.bookshop.repository;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    List<BookEntity> findBooksByTitleContaining(String bookTitle);

    List<BookEntity> findBooksByPriceOldBetween(Integer min, Integer max);

    List<BookEntity> findBooksByPriceOldIs(Integer price);

    BookEntity findBookEntityById(Integer id);

    @Query("from BookEntity where is_bestseller=1")
    List<BookEntity> getBestsellers();

    @Query(value = "SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)",nativeQuery = true)
    List<BookEntity> getBooksWithMaxDiscount();

    Page<BookEntity> findBookEntityByTitleContaining(String bookTitle, Pageable nexPage);

    Page<BookEntity> findPageOfBooksByPubDateBetweenOrderByPubDate(Date dateFrom, Date dateTo, Pageable nextPage);
    Page<BookEntity> findBookEntityByOrderByPubDate(Pageable next);

    Page<BookEntity> findByPubDateBetweenOrderByPubDateDesc(Pageable nextPage,
                                                            Date dateFrom,
                                                            Date dateTo);

    Page<BookEntity> findBookEntityByGenre(GenreEntity genre, Pageable next);

    Page<BookEntity> findAll(Pageable next);

    @Query(value = "SELECT b FROM BookEntity AS b JOIN Book2AuthorEntity AS b2a ON b2a.bookId = b.id WHERE b2a.authorId =?1")
    Page<BookEntity> findBookEntityByAuthorId(Integer id, Pageable nextPage);

    @Query(value = "SELECT b FROM BookEntity AS b " +
            "JOIN Book2GenreEntity AS b2g ON b2g.bookId = b.id " +
            "JOIN GenreEntity AS g ON g.id = b2g.genreId WHERE g.parentId = ?1")
    Page<BookEntity> findAllByGenre_ParentId(GenreType type, Pageable nextPage);

    @Query(value = "SELECT b FROM BookEntity AS b JOIN b.tagList AS t WHERE t.id=?1")
    Page<BookEntity> getBookEntitiesByTagId(Integer id, Pageable nextPage);

    BookEntity findBookEntityBySlug(String slug);

    List<BookEntity> findBookEntitiesBySlugIn(List<String> slugs);

    @Query(value = "SELECT b" +
            " FROM BookEntity AS b " +
            " JOIN Book2UserEntity AS b2u ON b2u.bookId = b.id" +
            " JOIN Book2UserTypeEntity AS but ON but.id = b2u.typeId" +
            " WHERE but.code = 'CART' AND b2u.userId = ?1")
    List<BookEntity> getBooksCartUser(Integer userId);

    @Query(value =  "SELECT b" +
            " FROM BookEntity AS b " +
            " JOIN Book2UserEntity AS b2u ON b2u.bookId = b.id" +
            " JOIN Book2UserTypeEntity AS but ON but.id = b2u.typeId" +
            " WHERE but.code = 'KEPT' AND b2u.userId = ?1")
    List<BookEntity> getBooksPostponedUser(Integer userId);

    @Query(value =  "SELECT b" +
            " FROM BookEntity AS b " +
            " JOIN Book2UserEntity AS b2u ON b2u.bookId = b.id" +
            " JOIN Book2UserTypeEntity AS but ON but.id = b2u.typeId" +
            " WHERE but.code = 'ARCHIVED' AND b2u.userId = ?1")
    List<BookEntity> getBooksArchive(Integer userId);

    @Query(value =  "SELECT b" +
            " FROM BookEntity AS b " +
            " JOIN Book2UserEntity AS b2u ON b2u.bookId = b.id" +
            " JOIN Book2UserTypeEntity AS but ON but.id = b2u.typeId" +
            " WHERE but.code = 'PAID' AND b2u.userId = ?1")
    List<BookEntity> getBooksPaid(Integer userId);

    @Query(value = "SELECT b FROM BookEntity AS b " +
            "JOIN ViewedBooks AS vb ON vb.book = b " +
            "WHERE vb.user = ?1")
    Page<BookEntity> getViewedBooksUser(UserEntity userId, Pageable nextPage);

    @Modifying
    @Query(value = "UPDATE BookEntity b SET b.popularity = :popular WHERE b.id = :bookId")
    void updatePopularityBook(@Param("popular") Integer popular, @Param("bookId") Integer id);

    @Modifying
    @Query(value = "UPDATE BookEntity b SET b.numberOfPosponed = :count WHERE b.slug = :slug")
    void updateCountPosponedBooks(@Param("slug") String slug, @Param("count") Integer count);

    @Modifying
    @Query(value = "UPDATE BookEntity b SET b.numberOfPurchased = :quantity WHERE b.slug = :slug")
    void updateCountPaidBooks(@Param("slug") String slug, @Param("quantity") Integer quantity);

    @Modifying
    @Query(value = "UPDATE book AS b SET quantity_basket = :quantity WHERE b.slug = :slug", nativeQuery = true)
    void updateCountCartBooks(@Param("slug") String slug, @Param("quantity") Integer quantity);

    @Query(value = "SELECT b FROM BookEntity AS b JOIN ViewedBooks AS vb ON vb.book = b WHERE vb.type='VIEWED'")
    Page<BookEntity> findBookEntityByViewed(Pageable  nextPage);

    @Query(value = "SELECT b FROM BookEntity AS b JOIN RatingBookEntity AS rb" +
            " ON rb.book = b ORDER BY rb.fiveStar DESC")
    Page<BookEntity> findAllOrderByRating(Pageable pageable);

}
