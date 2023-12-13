package com.example.bookshop.service.impl;

import com.example.bookshop.aop.annotations.LogSQLException;
import com.example.bookshop.dto.BookDto;
import com.example.bookshop.exeption.BookStoreApiWrongParameterException;
import com.example.bookshop.repository.*;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.BooksRatingAndPopulatityService;
import com.example.bookshop.service.util.DateFormatter;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bookshop.struct.book.links.Book2UserTypeEntity.StatusBookType.ARCHIVED;
import static com.example.bookshop.struct.book.links.Book2UserTypeEntity.StatusBookType.PAID;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    private final BookReviewRepository bookReviewRepository;

    @Autowired
    private final Book2UserRepository book2UserRepository;

    @Autowired
    private final Book2UserTypeRepository book2UserTypeRepository;

    @Autowired
    private final BookstoreUserRegister registerUser;

    @Autowired
    private final BooksRatingAndPopulatityService booksRatingAndPopulatityService;

    @Autowired
    private final BalanceTransactionRepository transactionRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<BookEntity> getBooksByTitle(String title) throws BookStoreApiWrongParameterException {

        if (title.equals("") || title.length() <= 1) {
            throw new BookStoreApiWrongParameterException("Wrong values paseed to one more parameters");
        } else {
            List<BookEntity> data = bookRepository.findBooksByTitleContaining(title);
            if (data.size() > 0) {
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
        return bookRepository.findBookEntityByViewed(nexPage);
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
    public void getBooksTheCartOfUser(Model model) {
        Object user = registerUser.getCurrentUser();
        if (user instanceof BookstoreUserDetails) {
            List<BookEntity> listBooksCartUser = bookRepository.getBooksCartUser(((BookstoreUserDetails) user).getContact().getUserId().getId());
            if (listBooksCartUser == null) {
                model.addAttribute("isEmptyCart", true);
            } else {
                model.addAttribute("isEmptyCart", false);
                model.addAttribute("bookCart", listBooksCartUser);
                for (BookEntity book : listBooksCartUser) {
                    model.addAttribute("totalRating", booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
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
            model.addAttribute("isPostponedEmpty", false);
            model.addAttribute("postponedBooks",
                    getListPostponedBooks(((BookstoreUserDetails) curUser).getContact().getUserId()));
            for (BookEntity book : getListPostponedBooks(
                    ((BookstoreUserDetails) curUser).getContact().getUserId())) {
                model.addAttribute("totalRating",
                        booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
            }
        } else {
            model.addAttribute("isPostponedEmpty", true);
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
    public void saveBook2User(BookEntity book, UserEntity user, Book2UserTypeEntity.StatusBookType type) {
        Book2UserEntity newBook2User =
                book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId());
        Book2UserTypeEntity book2UserType = book2UserTypeRepository.findByCode(type);
        try {
            if (newBook2User == null) {
                book.setStatus(type);
                bookRepository.save(book);
                Book2UserEntity b = new Book2UserEntity();
                b.setTime(new Date());
                b.setUserId(user.getId());
                b.setBookId(book.getId());
                b.setTypeId(book2UserType.getId());
                book2UserRepository.save(b);

            }
            else if (nonNull(newBook2User) && !newBook2User.getTypeId().equals(type) &&
                    !newBook2User.getTypeId().equals(PAID)
                    && !newBook2User.getTypeId().equals(ARCHIVED)) {
                book.setStatus(type);
                bookRepository.save(book);
                newBook2User.setTime(new Date());
                newBook2User.setTypeId(book2UserType.getId());
                book2UserRepository.saveAndFlush(newBook2User);
            } else if (nonNull(newBook2User) && newBook2User.getTypeId().equals(PAID)) {
                book.setStatus(type);
                bookRepository.save(book);
                newBook2User.setTime(new Date());
                newBook2User.setTypeId(book2UserType.getId());
                book2UserRepository.save(newBook2User);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<BookEntity> getBooksArchiveUser(Integer userId){
        return bookRepository.getBooksArchive(userId);
    }

    @Override
    public void removeBook2User(BookEntity book, UserEntity user){
        Book2UserEntity book2User =
                book2UserRepository.findBook2UserEntityByUserIdAndBookId(user.getId(), book.getId());
        book2UserRepository.delete(book2User);
    }

    @Override
    public void purchaseOfBooksByTheUser(UserEntity user, Model model){
        List<BookEntity> bookList = bookRepository.getBooksCartUser(user.getId());
        Double allSumBooks = bookList.stream().mapToDouble(BookEntity::getPrice).sum();

        if (user.getBalance() < allSumBooks){
            model.addAttribute("error", "true");
            model.addAttribute("noMoneyAccount", "На Вашем счету не достаточно денежных средств!!!");
        } else {
            bookList.forEach(book -> saveBook2User(book, user, PAID));
            BalanceTransactionEntity transaction = new BalanceTransactionEntity();
            String bookStrong = (bookList.size() == 1) ? "книги: " : "книг: ";
            String booksName = bookList.stream().map(book -> book.getTitle() + ", ").collect(Collectors.joining());
            transaction.setUserId(user);
            transaction.setDescription("Покупка " + bookStrong + booksName);
            transaction.setValue(allSumBooks);
            transaction.setTime(LocalDateTime.now());
            transactionRepository.save(transaction);
        }
    }

    @Override
    public boolean isStatus(BookEntity book){
        Object user = registerUser.getCurrentUser();
        Book2UserEntity b2u = book2UserRepository
                .findBook2UserEntityByUserIdAndBookId
                        (((BookstoreUserDetails)user).getContact().getUserId().getId(), book.getId());
        if (b2u == null){
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void updateCountPostponedBook(String slug, Integer post){
        bookRepository.updateCountPosponedBooks(slug, post);
    }

    @Override
    public Page<BookEntity> getListViewedBooksUser(Integer offset, Integer limit, HttpServletRequest request){
        Pageable page = PageRequest.of(offset, limit);
        Cookie[] cookies = request.getCookies();
        String hashUser = "";
        Object curUser = registerUser.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails){
            return bookRepository.getViewedBooksUser(((BookstoreUserDetails) curUser).getContact().getUserId(), page);
        } else {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("USER-ANONYMOUS")) {
                    hashUser = cookie.getValue();
                }
            }
            UserEntity anonyUser  = userRepository.findByHash(hashUser);
            return bookRepository.getViewedBooksUser(anonyUser, page);
        }
    }
    @Transactional
    @Override
    public void updateCountBooksCart(String slug, Integer count){
        bookRepository.updateCountCartBooks(slug,  count);

    }

    @Override
    public void addBook(BookDto bookDto) {
        BookEntity newBook = new BookEntity();
        newBook.setTitle(bookDto.getTitle());
        newBook.setDescription(bookDto.getDescription());
        newBook.setPrice(Double.parseDouble(bookDto.getPrice()));
        newBook.setPriceOld(Integer.parseInt(bookDto.getDiscountPrice()));
        newBook.setPubDate(DateFormatter.getToDateFormat(bookDto.getPubDate()));
        newBook.setImage(bookDto.getImage());
        newBook.setAuthors(bookDto.getAuthors());
        newBook.setTagList(bookDto.getTags());
        newBook.setGenre(bookDto.getGenre());
        newBook.setIsBesteller(isBestsellerBook(bookDto.getBestseller()));
        newBook.setSlug(randomSlug());
        bookRepository.save(newBook);

    }

    public String randomSlug(){
        return RandomStringUtils.randomAlphabetic(6);
    }

    public Integer isBestsellerBook(String bestseller){
        if (bestseller.equals("no")){
            return 0;
        }
         return 1;
    }

    @Transactional
    @Override
    public void updateCountCartAndCountPostponed(String slug, Integer countCart, Integer countPostponed){
        updateCountPostponedBook(slug, countPostponed);
        updateCountBooksCart(slug, countCart);
    }

    @Transactional
    @Override
    public void updateCountPaidBooks(String slug, Integer count){
        bookRepository.updateCountPaidBooks(slug, count);
    }

    @Override
    public BookEntity findBookById(Integer id) {
        return bookRepository.findBookEntityById(id);
    }


}
