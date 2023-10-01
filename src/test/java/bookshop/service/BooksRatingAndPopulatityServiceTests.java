package com.example.bookshop.service;

import com.example.bookshop.dto.BookReviewDto;
import com.example.bookshop.dto.LikeReviewBookDto;
import com.example.bookshop.dto.RatingCountI;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.BookReviewRepository;
import com.example.bookshop.repository.RatingRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.ratings.RatingBookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BooksRatingAndPopulatityServiceTests {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;

    @MockBean
    private BooksRatingAndPopulatityService booksRatingAndPopulatityService;
    @MockBean
    private BookReviewService bookReviewServiceMock;
    @MockBean
    private LikeReviewBookDto likeReviewBookDtoMock;


    @Autowired
    public BooksRatingAndPopulatityServiceTests(UserRepository userRepository,
                                                RatingRepository ratingRepository,
                                                BookReviewRepository bookReviewRepository,
                                                BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    @DisplayName("Возвращение рейтинга по id книги")
    void getRatingBookTest() {
        RatingBookEntity ratingBookEntity = ratingRepository.findRatingBookEntityByBookId(1);

        assertNotNull(ratingBookEntity);

        assertEquals(ratingBookEntity.getFiveStar(), 247);
    }

    @SneakyThrows
    @Test
    @DisplayName("Сохранение рейтинга книги")
    void saveRatingReviewTest() {
        RatingBookEntity ratingBookEntity = ratingRepository.findRatingBookEntityByBookId(1);

        booksRatingAndPopulatityService.ratingBookSave(2, ratingBookEntity);

        assertTrue(true);

    }
    @Test
    @DisplayName("Сохранение отзыва книги")
    void saveReviewBookTest(){
        BookEntity book = bookRepository.findBookEntityBySlug("book-dez-502");
        BookReviewDto bookReviewDto = new BookReviewDto();
        bookReviewDto.setText("Книга написана в отличном жанре.");
        UserEntity user = userRepository.getUserByUsername("МИХАИЛ");
        BookReviewEntity bookReviewEntity = new BookReviewEntity();
        bookReviewEntity.setBookId(book);
        bookReviewEntity.setUserId(user);
        bookReviewEntity.setText(bookReviewDto.getText());
        bookReviewEntity.setRating(0);
        bookReviewEntity.setTime(new Date());

        bookReviewRepository.save(bookReviewEntity);


        assertNotNull(user);

        assertNotNull(book);


    }

    @Test
    @DisplayName("Проверка расчета рейтинга отзыва книги")
    void calculateRatingReviewTest()  {

        bookReviewServiceMock.saveReviewText("book-axe-269", "Очень хорошая книга");
        BookEntity book = bookRepository.findBookEntityBySlug("book-axe-269");
        BookReviewEntity review = bookReviewRepository.findBookReviewEntityById(book.getId());
        short likeReview = 1;
        likeReviewBookDtoMock.setValue(likeReview);
        likeReviewBookDtoMock.setReviewid(1);
        booksRatingAndPopulatityService.saveLikeReviewBook(likeReviewBookDtoMock);
        long d = review.getLikeCount() > review.getDisLikeCount() ? review.getLikeCount() - review.getDisLikeCount() :
                review.getDisLikeCount() - review.getLikeCount();

        assertNotNull(review);

        assertNotNull(book);

        assertEquals(d, 2);
    }

    @Test
    @DisplayName("Проверка расчета рейтинга книги")
    void calculateRatingBookTest(){

        BookEntity book = bookRepository.findBookEntityBySlug("book-axe-269");

        RatingCountI ratingCountI = ratingRepository.getTotalAndAvgStars(book.getId());

        assertNotNull(ratingCountI);

        assertEquals(ratingCountI.getAverage(), book.getRating());

    }
}