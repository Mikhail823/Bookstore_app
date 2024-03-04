package com.example.bookshop.controllers;

import com.example.bookshop.dto.SearchWordDto;
import com.example.bookshop.repository.Book2UserRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.struct.book.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@ControllerAdvice
public class GlobalModelAttributesController {

    public static final String KEPT = "KEPT";
    public static final String CART = "CART";

    private final BookService bookService;
    private final BookstoreUserRegister userRegister;
    private final CookieService cookieComponent;
    private final UserService userService;
    private final Book2UserRepository bookUserRepository;

    @Autowired
    public GlobalModelAttributesController(BookService bookService,
                                           BookstoreUserRegister userRegister,
                                           CookieService cookieComponent, UserService userService,
                                           Book2UserRepository bookUserRepository) {
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.cookieComponent = cookieComponent;
        this.userService = userService;
        this.bookUserRepository = bookUserRepository;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto dto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResult")
    public List<BookEntity> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("curUser")
    public Object getCurrentUserBookShop() {
        Object curUser = userRegister.getCurrentUser();

        return (curUser instanceof BookstoreUserDetails) ?
                ((BookstoreUserDetails) curUser).getContact().getUserId().getName() : "Гость";
    }

    @ModelAttribute
    public String getCountBooksCart(Model model) {
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            model.addAttribute("countBooksCart",
                    bookUserRepository.getCountBooksCart(((BookstoreUserDetails) curUser).getContact().getUserId().getId(), 2) != 0 ?
                            bookUserRepository.getCountBooksCart(((BookstoreUserDetails) curUser).getContact().getUserId().getId(), 2) : 0);
        }
        return "fragments/count_book_fragment :: countBooks";
    }

    @ModelAttribute("countBooksCartAnyUser")
    public Integer getCountBooksCartAnyUser(@CookieValue(name = "cartContents", required = false) String cartContents) {
        return (cookieComponent.countBooksCookie(cartContents, CART) == 0) ? 0
                : cookieComponent.countBooksCookie(cartContents, CART);
    }

    @ModelAttribute("countPostponedBooksAnyUser")
    public Integer getCountBooksPostponedAnyUser(@CookieValue(name = "postponedBook", required = false) String postponedBook) {

        return (cookieComponent.countBooksCookie(postponedBook, KEPT) == 0) ? 0
                : cookieComponent.countBooksCookie(postponedBook, KEPT);
    }

    @ModelAttribute("countBooksPostponed")
    public Integer getCountBooksPostponed() {
        Object curUser = userRegister.getCurrentUser();

        return (curUser instanceof BookstoreUserDetails) ?
                bookService.getListPostponedBooks(((BookstoreUserDetails) curUser).getContact().getUserId()).size() : 0;
    }

    @ModelAttribute
    public String getAccountMoney(Model model) {
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails){
            model.addAttribute("accountMoney",
                    (userService.getUserBalance(((BookstoreUserDetails) curUser).getContact().getUserId()) != 0) ?
                            userService.getUserBalance(((BookstoreUserDetails) curUser).getContact().getUserId()) : 0.0);
        }
        return "fragments/money_fragment :: money";
    }

    @ModelAttribute("countNotReadBook")
    public Integer getCountNotReadBook() {
        Object curUser = userRegister.getCurrentUser();
        return (curUser instanceof BookstoreUserDetails) ?
                bookService.getNotReadBooks(((BookstoreUserDetails) curUser)
                        .getContact().getUserId().getId()).size() : 0;

    }


}
