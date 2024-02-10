package com.example.bookshop.controllers;

import com.example.bookshop.dto.SearchWordDto;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.struct.book.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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

    @Autowired
    public GlobalModelAttributesController(BookService bookService,
                                           BookstoreUserRegister userRegister,
                                           CookieService cookieComponent) {
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.cookieComponent = cookieComponent;
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

    @ModelAttribute("countBooksCart")
    public Integer getCountBooksCart() {
        Object curUser = userRegister.getCurrentUser();

        return (curUser instanceof BookstoreUserDetails) ?
                bookService.getBooksCart(((BookstoreUserDetails) curUser).getContact().getUserId()).size() : 0;
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

    @ModelAttribute("accountMoney")
    public Double getAccountMoney() {
        Object curUser = userRegister.getCurrentUser();
        return (curUser instanceof BookstoreUserDetails &&
                ((BookstoreUserDetails) curUser).getContact().getUserId().getBalance() != 0) ?
                ((BookstoreUserDetails) curUser).getContact().getUserId().getBalance() : 0.0;
    }

    @ModelAttribute("countNotReadBook")
    public Integer getCountNotReadBook() {
        Object curUser = userRegister.getCurrentUser();
        return (curUser instanceof BookstoreUserDetails) ?
                bookService.getNotReadBooks(((BookstoreUserDetails) curUser)
                        .getContact().getUserId().getId()).size() : 0;

    }


}
