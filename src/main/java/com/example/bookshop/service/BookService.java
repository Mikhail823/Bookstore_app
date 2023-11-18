package com.example.bookshop.service;

import com.example.bookshop.exeption.BookStoreApiWrongParameterException;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


public interface BookService {

//    List<BookEntity> getBooksByAuthor(String authorName);

    List<BookEntity> getBooksByTitle(String title) throws BookStoreApiWrongParameterException;

    List<BookEntity> getBooksWithPriceBetween(Integer min, Integer max);

    List<BookEntity> getBooksWithMaxPrice();

    List<BookEntity> getBestsellers();

    Page<BookEntity> getPageOfRecommendedBooks(Integer offset, Integer limit);

    Page<BookEntity> getPageOfSearchResultsBooks(String searchWord, Integer offset, Integer limit);

    Page<BookEntity> getPageOfRecentBooksData(Date dateFrom, Date dateTo, Integer offset, Integer limit);

    Page<BookEntity> getPageOfPopularBooks(Integer offset, Integer limit);

    Page<BookEntity> getPageRecentSlider(Integer offset, Integer limit);

    Page<BookEntity> getBooksOfGenre(GenreEntity genre, Integer offset, Integer limit);

    Page<BookEntity> getBooksByAuthorId(Integer id, Integer offset, Integer limit);

    Page<BookEntity> getBooksOfGenreType(GenreType type, Integer offset, Integer limit);

    Page<BookEntity> getBooksOfTags(Integer tagId, Integer offset, Integer limit);

    BookEntity getBookPageSlug(String slugBook);

    BookEntity saveImageBook(BookEntity book);

    List<BookEntity> getBooksBySlugIn(List<String> slugs);

    List<BookReviewEntity> getBookReview(BookEntity book, Integer offset, Integer limit);

    List<BookEntity> getBooksCart(UserEntity userId);

   void getBooksTheCartOfUser(Model model);

    void calculationCostOfBooksTheCartUser(Model model, List<BookEntity> listBook);

    void getPostponedBooksOfUser(Model model);

    List<BookEntity> getListPostponedBooks(UserEntity userId);

    List<BookEntity> getNotReadBooks(Integer userId);

    void saveBook2User(BookEntity book, UserEntity user, Book2UserTypeEntity.StatusBookType type);

    List<BookEntity> getBooksArchiveUser(Integer userId);

    void removeBook2User(BookEntity book, UserEntity user);

    void purchaseOfBooksByTheUser(UserEntity user, Model model);

    boolean isStatus(BookEntity book);


    void updateCountPostponedBook(String slug, Integer post);

    Page<BookEntity> getListViewedBooksUser(Integer offset, Integer limit, HttpServletRequest request);

    void updateCountBooksCart(String slug, Integer count);

    void updateCountCartAndCountPostponed(String slug, Integer countCart, Integer countPostponed);

    void updateCountPaidBooks(String slug, Integer count);
}
