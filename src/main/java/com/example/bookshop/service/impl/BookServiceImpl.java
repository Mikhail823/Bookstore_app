package com.example.bookshop.service.impl;

import com.example.bookshop.aop.annotations.LogSQLException;
import com.example.bookshop.dto.BookDto;
import com.example.bookshop.exeption.BookStoreApiWrongParameterException;
import com.example.bookshop.repository.*;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.BooksRatingAndPopulatityService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.service.util.DateFormatter;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.bookshop.struct.book.links.Book2UserTypeEntity.StatusBookType.*;

@Service
public class BookServiceImpl implements BookService {

    public static final String POSTPONED_BOOK = "postponedBook";
    public static final String IS_POSTPONED_EMPTY = "isPostponedEmpty";
    public static final String IS_CART_EMPTY = "isEmptyCart";
    public static final String BOOK_CART = "bookCart";
    public static final String TOTAL_RATING = "totalRating";

    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final BookstoreUserRegister registerUser;
    private final BooksRatingAndPopulatityService booksRatingAndPopulatityService;
    private final BalanceTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private DateFormatter dateFormatter = new DateFormatter();

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookReviewRepository bookReviewRepository,
                           Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository,
                           BookstoreUserRegister registerUser,
                           BooksRatingAndPopulatityService booksRatingAndPopulatityService,
                           BalanceTransactionRepository transactionRepository, UserRepository userRepository,
                           @Lazy CookieService cookieService) {
        this.bookRepository = bookRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
        this.registerUser = registerUser;
        this.booksRatingAndPopulatityService = booksRatingAndPopulatityService;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.cookieService = cookieService;
    }

    @Override
    public List<BookEntity> getBooksByTitle(String title) throws BookStoreApiWrongParameterException {

        if (title.length() <= 1) {
            throw new BookStoreApiWrongParameterException("Wrong values paseed to one more parameters");
        } else {
            List<BookEntity> data = bookRepository.findBooksByTitleContaining(title);
            if (data.isEmpty()) {
                return data;
            } else {
                throw new BookStoreApiWrongParameterException("Data not found parameters!");
            }
        }
    }

    @Override
    public List<BookEntity> getBooksWithPriceBetween(Integer min, Integer max) {
        return bookRepository.findBooksByPriceOldBetween(min, max);
    }

    @Override
    public List<BookEntity> getBooksWithMaxPrice() {
        return bookRepository.getBooksWithMaxDiscount();
    }

    @Override
    public List<BookEntity> getBestsellers() {
        return bookRepository.getBestsellers();
    }

    @Override
    public Page<BookEntity> getPageOfRecommendedBooks(Integer offset, Integer limit) {
        Pageable nexPage = PageRequest.of(offset, limit);
        return bookRepository.findAllByIsBestellerOrderByPubDateDesc(nexPage);
    }

    @Override
    public Page<BookEntity> getPageOfSearchResultsBooks(String searchWord, Integer offset, Integer limit) {
        Pageable nexPage = PageRequest.of(offset, limit);
        return bookRepository.findBookEntityByTitleContaining(searchWord, nexPage);
    }

    @Override
    public Page<BookEntity> getPageOfRecentBooksData(Date dateFrom, Date dateTo, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findPageOfBooksByPubDateBetweenOrderByPubDate(dateFrom, dateTo, nextPage);
    }

    @Override
    public Page<BookEntity> getPageOfPopularBooks(Integer offset, Integer limit) {
        Pageable nexPage = PageRequest.of(offset, limit);
        return bookRepository.findAllOrderByRating(nexPage);
    }

    @Override
    public Page<BookEntity> getPageRecentSlider(Integer offset, Integer limit) {
        Pageable nextPageRecent = PageRequest.of(offset, limit);
        return bookRepository.findAll(nextPageRecent);
    }

    @Override
    public Page<BookEntity> getBooksOfGenre(GenreEntity genre, Integer offset, Integer limit) {
        Pageable next = PageRequest.of(offset, limit);
        return bookRepository.findBookEntityByGenre(genre, next);
    }

    @Override
    public Page<BookEntity> getBooksByAuthorId(Integer id, Integer offset, Integer limit) {
        Pageable next = PageRequest.of(offset, limit);
        return bookRepository.findBookEntityByAuthorId(id, next);
    }

    @Override
    public Page<BookEntity> getBooksOfGenreType(GenreType type, Integer offset, Integer limit) {
        Pageable next = PageRequest.of(offset, limit);
        return bookRepository.findAllByGenre_ParentId(type, next);
    }

    @Override
    public Page<BookEntity> getBooksOfTags(Integer tagId, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.getBookEntitiesByTagId(tagId, nextPage);
    }

    @Override
    public BookEntity getBookPageSlug(String slugBook) {
        return bookRepository.findBookEntityBySlug(slugBook);
    }

    @Override
    public BookEntity saveImageBook(BookEntity book) {
        return bookRepository.save(book);
    }

    @Override
    public List<BookEntity> getBooksBySlugIn(List<String> slugs) {
        return bookRepository.findBookEntitiesBySlugIn(slugs);
    }

    @Override
    public List<BookReviewEntity> getBookReview(BookEntity book, Integer offset, Integer limit) {
        Pageable limitPageReview = PageRequest.of(offset, limit);
        return bookReviewRepository.findBookReviewEntitiesByBookId(book, limitPageReview);
    }

    @Override
    public List<BookEntity> getBooksCart(UserEntity userId) {
        return bookRepository.getBooksCartUser(userId.getId());
    }
    @Override
    public Integer getCountBooksTheCart(UserEntity user){
        return bookRepository.getBooksCartUser(user.getId()).size();
    }

    @Override
    public void getBooksTheCartOfUser(Model model) {
        Object user = registerUser.getCurrentUser();
        if (user instanceof BookstoreUserDetails) {
            List<BookEntity> listBooksCartUser = bookRepository.getBooksCartUser(((BookstoreUserDetails) user).getContact().getUserId().getId());
            if (listBooksCartUser == null) {
                model.addAttribute(IS_CART_EMPTY, true);
            } else {
                model.addAttribute(IS_CART_EMPTY, false);
                model.addAttribute(BOOK_CART , listBooksCartUser);
                for (BookEntity book : listBooksCartUser) {
                    model.addAttribute(TOTAL_RATING, booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
                }
                calculationCostOfBooksTheCartUser(model, listBooksCartUser);
            }
        }
    }

    @Override
    public void calculationCostOfBooksTheCartUser(Model model, List<BookEntity> listBook) {
        int totalPrice = 0;
        int oldPrice = 0;
        for (BookEntity book : listBook) {
            totalPrice = totalPrice + book.discountPrice();
            oldPrice = oldPrice + book.getPriceOld();
        }
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("oldPrice", oldPrice);

    }

    @Override
    public void getPostponedBooksOfUser(Model model) {
        Object curUser = registerUser.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            model.addAttribute(IS_POSTPONED_EMPTY, false);
            model.addAttribute(POSTPONED_BOOK,
                    getListPostponedBooks(((BookstoreUserDetails) curUser).getContact().getUserId()));
            for (BookEntity book : getListPostponedBooks(
                    ((BookstoreUserDetails) curUser).getContact().getUserId())) {
                model.addAttribute(TOTAL_RATING,
                        booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
            }
        } else {
            model.addAttribute(IS_POSTPONED_EMPTY, true);
        }
    }

    @Override
    public List<BookEntity> getListPostponedBooks(UserEntity userId) {
        return bookRepository.getBooksPostponedUser(userId.getId());
    }

    @Override
    public List<BookEntity> getNotReadBooks(Integer userId) {
        return bookRepository.getBooksPaid(userId);
    }

    @LogSQLException
    @Transactional
    @Override
    public void saveBookUser(BookEntity book, UserEntity user, Book2UserTypeEntity.StatusBookType type) {
        Book2UserEntity newBookUser =
                book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId());
        Book2UserTypeEntity bookUserType = book2UserTypeRepository.findByCode(type);

        if (newBookUser == null) {
            saveNewBookUser(book, user, bookUserType);
        } else {
            book2UserRepository.updateBookUser(user.getId(), book.getId(), bookUserType.getId());
        }
    }

    public void saveNewBookUser(BookEntity book, UserEntity user, Book2UserTypeEntity type){
        book.setStatus(type.getCode());
        bookRepository.save(book);
        Book2UserEntity newBookUser = new Book2UserEntity();
        newBookUser.setTime(new Date());
        newBookUser.setUserId(user.getId());
        newBookUser.setBookId(book.getId());
        newBookUser.setTypeId(type.getId());
        book2UserRepository.save(newBookUser);
    }

    @Override
    public List<BookEntity> getBooksArchiveUser(Integer userId) {
        return bookRepository.getBooksArchive(userId);
    }

    @Override
    @Transactional
    public void removeBookUser(BookEntity book, UserEntity user) {
        Book2UserEntity bookUser =
                book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId());
        if (bookUser != null) {
            book2UserRepository.delete(bookUser);
        }
    }

    @Override
    @Transactional
    public void purchaseOfBooksByTheUser(UserEntity user, Model model) {
        List<BookEntity> bookList = bookRepository.getBooksCartUser(user.getId());
        double allSumBooks = bookList.stream().mapToDouble(BookEntity::getPrice).sum();

        if (user.getBalance() < allSumBooks) {
            model.addAttribute("error", true);
            model.addAttribute("noMoneyAccount", "На Вашем счету не достаточно денежных средств!!!");
        } else {
            bookList.forEach(book -> saveBookUser(book, user, PAID));
            BalanceTransactionEntity transaction = new BalanceTransactionEntity();
            String bookStrong = (bookList.size() == 1) ? "книги: " : "книг: ";
            String booksName = bookList.stream().map(book -> book.getTitle() + " ").collect(Collectors.joining());
            transaction.setUserId(user);
            transaction.setDescription("Покупка " + bookStrong + booksName);
            transaction.setValue(allSumBooks);
            transaction.setTime(LocalDateTime.now());
            transactionRepository.save(transaction);
        }
    }

    @Override
    public boolean isStatus(BookEntity book) {
        Object user = registerUser.getCurrentUser();
        Book2UserEntity bookUser = book2UserRepository
                .findBook2UserEntityByUserIdAndBookId
                        (((BookstoreUserDetails) user).getContact().getUserId().getId(), book.getId());
        return bookUser != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateCountPostponedBook(String slug, Integer post) {
        bookRepository.updateCountPosponedBooks(slug, post);
    }

    @Override
    public Page<BookEntity> getListViewedBooksUser(Integer offset, Integer limit) {
        Pageable page = PageRequest.of(offset, limit);
        Object curUser = registerUser.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            return bookRepository
                    .getViewedBooksUser(((BookstoreUserDetails) curUser).getContact()
                            .getUserId().getId(), page);
        } else {
            return bookRepository.findAll(page);
        }
    }

    @Override
    @Transactional
    public void updateCountBooksCart(String slug, Integer count) {
        bookRepository.updateCountCartBooks(slug, count);
    }

    @Override
    public void addBook(BookDto bookDto) {
        BookEntity newBook = new BookEntity();
        newBook.setTitle(bookDto.getTitle());
        newBook.setDescription(bookDto.getDescription());
        newBook.setPrice(Double.parseDouble(bookDto.getPrice()));
        newBook.setPriceOld(Integer.parseInt(bookDto.getDiscountPrice()));
        newBook.setPubDate(dateFormatter.getToDateFormat(bookDto.getPubDate()));
        newBook.setImage(bookDto.getImage());
        newBook.setAuthors(bookDto.getAuthors());
        newBook.setTagList(bookDto.getTags());
        newBook.setGenre(bookDto.getGenre());
        newBook.setIsBesteller(bestsellerBook(bookDto.getBestseller()));
        newBook.setSlug(randomSlug());
        bookRepository.save(newBook);

    }

    public String randomSlug() {
        return RandomStringUtils.randomAlphabetic(6);
    }

    public Integer bestsellerBook(String bestseller) {
        return (bestseller.equals("no")) ? 0 : 1;
    }

    @Transactional
    @Override
    public void updateCountCartAndCountPostponed(String slug, Integer countCart, Integer countPostponed) {
        updateCountPostponedBook(slug, countPostponed);
        updateCountBooksCart(slug, countCart);
    }


    @Override
    public void updateCountPaidBooks(String slug, Integer count) {
        bookRepository.updateCountPaidBooks(slug, count);
    }

    @Override
    public BookEntity findBookById(Integer id) {
        return null;
    }

    @Override
    @Transactional
    public String addingBookStatusCart(String slug, Model model,
                                       HttpServletResponse response,
                                       HttpServletRequest request,
                                       String cartContents,
                                       String redirect, Map<String, String> allParams) {
        Object curUser = registerUser.getCurrentUser();
        BookEntity book = getBookPageSlug(slug);
        int countCartBooks = book.getQuantityTheBasket() == null ? 0 : book.getQuantityTheBasket();
        if (curUser instanceof BookstoreUserDetails) {
            UserEntity user = userRepository.findUserEntityById(((BookstoreUserDetails) curUser).getContact().getUserId().getId());
            saveBookUser(book, user, CART);
            updateCountBooksCart(slug, countCartBooks + 1);
            return redirect + slug;
        } else {
            cookieService.addBooksCookieCart(cartContents, model, allParams, response, request, slug);
            updateCountBooksCart(slug, countCartBooks + 1);
            return redirect + slug;
        }
    }
}
