package com.example.bookshop.service.impl;

import com.example.bookshop.dto.LikeReviewBookDto;
import com.example.bookshop.dto.RatingCountDto;
import com.example.bookshop.dto.RatingCountI;
import com.example.bookshop.repository.*;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.security.exception.RequestException;
import com.example.bookshop.security.exception.UserNotFoundException;
import com.example.bookshop.service.BooksRatingAndPopulatityService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.ratings.RatingBookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.book.review.BookReviewLikeEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BooksRatingAndPopulatityServiceImpl implements BooksRatingAndPopulatityService {

    @Autowired
    private final RatingRepository ratingRepository;

    @Autowired
    private final BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    private final BookstoreUserRegister bookstoreUserRegister;

    @Autowired
    private final UserService userServiceImp;

    @Autowired
    private final BookRepository bookRepository;

    @Override
    public RatingCountDto getTotalAndAvgStars(Integer bookId) {
        RatingCountI totalAndAvgStars = ratingRepository.getTotalAndAvgStars(bookId);
        BookEntity book = bookRepository.getOne(bookId);
        if (totalAndAvgStars == null){
            book.setRating(0);
            bookRepository.save(book);
            return new RatingCountDto(0, 0);
        } else {
            book.setRating(totalAndAvgStars.getAverage());
            bookRepository.save(book);
            return new RatingCountDto(totalAndAvgStars.getTotal(), totalAndAvgStars.getAverage());
        }
    }

    @Override
    public RatingBookEntity getRatingBook(Integer id) {
        return ratingRepository.findRatingBookEntityByBookId(id);
    }

    @Override
    public void ratingBookSave(Integer value, RatingBookEntity ratingBookEntity) throws RequestException {

        switch (value) {
            case 1:
                ratingBookEntity.setOneStar(ratingBookEntity.getOneStar() + 1);
                break;
            case 2:
                ratingBookEntity.setTwoStar(ratingBookEntity.getTwoStar() + 1);
                break;
            case 3:
                ratingBookEntity.setThreeStar(ratingBookEntity.getThreeStar() + 1);
                break;
            case 4:
                ratingBookEntity.setFourStar(ratingBookEntity.getFourStar() + 1);
                break;
            case 5:
                ratingBookEntity.setFiveStar(ratingBookEntity.getFiveStar() + 1);
                break;
        }
        ratingRepository.save(ratingBookEntity);

    }

    @Override
    public BookReviewLikeEntity saveLikeReviewBook(LikeReviewBookDto likeReviewBookDto) {
        Object curUser = bookstoreUserRegister.getCurrentUser();
        UserEntity user;
        BookReviewLikeEntity like = new BookReviewLikeEntity();
        if (curUser instanceof BookstoreUserDetails){
            user = userServiceImp.getUserName(((BookstoreUserDetails) curUser).getName());
            BookReviewEntity bookReview = bookReviewRepository.findBookReviewEntityById(likeReviewBookDto.getReviewid());

            like.setReviewId(bookReview);
            like.setUserId(user);
            like.setTime(new Date());
            like.setValue(likeReviewBookDto.getValue());

        }
        return bookReviewLikeRepository.save(like);

    }

    @Override
    public void saveRatingReview(Integer id) {
        if (!getCurrentUser().equals(null)) {
            BookReviewEntity review = bookReviewRepository.findBookReviewEntityById(id);
            if (review.getLikeCount() > 0 && review.getDisLikeCount() > 0) {
                long avg = review.getLikeCount() > review.getDisLikeCount() ?
                        review.getLikeCount() - review.getDisLikeCount() : review.getDisLikeCount() - review.getLikeCount();
                review.setRating(Math.toIntExact(avg));
                bookReviewRepository.save(review);
            } else if (review.getLikeCount() == 0 || review.getDisLikeCount() == 0) {
                review.setRating(0);
                bookReviewRepository.save(review);
            }
        } else {
            new UserNotFoundException("Данный пользователь не зарегистрирован((");
        }
    }

    @Override
    public Object getCurrentUser() {
        return bookstoreUserRegister.getCurrentUser();
    }

    @Transactional
    @Override
    public void calculatingThePopularityOfBook(BookEntity book){

        Integer quantityBookCart = book.getQuantityTheBasket() == null ? 0 : book.getQuantityTheBasket();
        Integer quantityBookPostponed = book.getNumberOfPosponed() == null ? 0 : book.getNumberOfPosponed();
        Integer quantityBookPaid = book.getNumberOfPurchased() == null ? 0 : book.getNumberOfPurchased();

        Integer popularity = (int)(quantityBookPaid +  0.7 * quantityBookCart
                + 0.4 * quantityBookPostponed);
        bookRepository.updatePopularityBook(popularity, book.getId());
    }
}
