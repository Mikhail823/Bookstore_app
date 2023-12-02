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

   // List<BookEntity> findBooksByAuthorFirstNameContaining(String authorsFirstName);

    List<BookEntity> findBooksByTitleContaining(String bookTitle);

    List<BookEntity> findBooksByPriceOldBetween(Integer min, Integer max);

    List<BookEntity> findBooksByPriceOldIs(Integer price);

    BookEntity findBookEntityById(Integer id);

   // List<BookEntity> findBookEntityByAuthor_FirstName(String name);

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

    Page<BookEntity> findBookEntityByGenre(Integer id, Pageable next);


    Page<BookEntity> findAll(Pageable next);

    @Query(value = "SELECT * FROM book AS b JOIN book2author AS b2a ON b2a.book_id = b.id WHERE b2a.author_id =:id", nativeQuery = true)
    Page<BookEntity> findBookEntityByAuthorId(@Param("id")Integer id, Pageable nextPage);

    Page<BookEntity> findAllByGenre_ParentId(GenreType type, Pageable nextPage);

    @Query(value = "SELECT b FROM BookEntity AS b JOIN b.tagList AS t WHERE t.id=?1")
    Page<BookEntity> getBookEntitiesByTagId(Integer id, Pageable nextPage);

    BookEntity findBookEntityBySlug(String slug);

    List<BookEntity> findBookEntitiesBySlugIn(List<String> slugs);

    @Query(value = "SELECT *" +
            " FROM book AS b " +
            " JOIN book2user AS b2u ON b2u.book_id = b.id" +
            " JOIN book2user_type AS but ON but.id = b2u.type_id" +
            " WHERE but.code = 'CART' AND b2u.user_id = ?1", nativeQuery = true)
    List<BookEntity> getBooksCartUser(Integer userId);

    @Query(value =  "SELECT *" +
            " FROM book AS b " +
            " JOIN book2user AS b2u ON b2u.book_id = b.id" +
            " JOIN book2user_type AS but ON but.id = b2u.type_id" +
            " WHERE but.code = 'KEPT' AND b2u.user_id = ?1", nativeQuery = true)
    List<BookEntity> getBooksPostponedUser(Integer userId);

    @Query(value =  "SELECT *" +
            " FROM book AS b " +
            " JOIN book2user AS b2u ON b2u.book_id = b.id" +
            " JOIN book2user_type AS but ON but.id = b2u.type_id" +
            " WHERE but.code = 'ARCHIVED' AND b2u.user_id = ?1", nativeQuery = true)
    List<BookEntity> getBooksArchive(Integer userId);

    @Query(value =  "SELECT *" +
            " FROM book AS b " +
            " JOIN book2user AS b2u ON b2u.book_id = b.id" +
            " JOIN book2user_type AS but ON but.id = b2u.type_id" +
            " WHERE but.code = 'PAID' AND b2u.user_id = ?1", nativeQuery = true)
    List<BookEntity> getBooksPaid(Integer userId);

    @Query(value = "SELECT * FROM book AS b " +
            "JOIN viewed_books AS vb ON vb.book_id = b.id WHERE vb.user_id = ?1", nativeQuery = true)
    Page<BookEntity> getViewedBooksUser(UserEntity userId, Pageable nextPage);

    @Modifying
    @Query(value = "UPDATE book b SET popularity=:popular WHERE id=:bookId", nativeQuery = true)
    void updatePopularityBook(@Param("popular") Integer popular, @Param("bookId") Integer id);

    @Modifying
    @Query(value = "UPDATE book b SET count_postponed=:count WHERE slug=:slug", nativeQuery = true)
    void updateCountPosponedBooks(@Param("slug") String slug, @Param("count") Integer count);

    @Modifying
    @Query(value = "UPDATE book b SET count_purchased=:quantity WHERE slug=:slug", nativeQuery = true)
    void updateCountPaidBooks(@Param("slug") String slug, @Param("quantity") Integer quantity);

    @Modifying
    @Query(value = "UPDATE book b SET quantity_basket=:quantity WHERE slug=:slug", nativeQuery = true)
    void updateCountCartBooks(@Param("slug") String slug, @Param("quantity") Integer quantity);

    @Query(value = "SELECT * FROM book AS b JOIN viewed_books AS vb ON vb.book_id=b.id WHERE vb.type='VIEWED'", nativeQuery = true)
    Page<BookEntity> findBookEntityByViewed(Pageable  nextPage);

    @Query(value = "SELECT * FROM book AS b JOIN viewed_books AS vb" +
            " ON vb.book_id=b.id WHERE vb.time >= NOW() - INTERVAL '7 DAY' ORDER BY b.rating DESC", nativeQuery = true)
    Page<BookEntity> findAllBooksPopulal(Pageable pageable);

}
