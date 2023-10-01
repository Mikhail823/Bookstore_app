package com.example.bookshop.controllers;

import com.example.bookshop.dto.SearchWordDto;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.struct.book.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
@Primary
public class GlobalModelAttributesController {
    @Autowired
    private final BookService bookService;
    @Autowired
    private final BookstoreUserRegister userRegister;
    @Autowired
    private final CookieService cookieComponent;

    @ModelAttribute("searchWordDto")
    public SearchWordDto dto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResult")
    public List<BookEntity> searchResults() {
        return new ArrayList<>();
    }

    @ModelAttribute("curUser")
    public Object getCurrentUserBookShop(){
        Object curUser = userRegister.getCurrentUser();

        if (curUser instanceof BookstoreUserDetails){
            return ((BookstoreUserDetails) curUser).getContact().getUserId().getName();
        }
        else {
            return "Гость";
        }
    }

    @ModelAttribute("countBooksCart")
    public Integer getCountBooksCart(){
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails){
            return bookService.getBooksCart(((BookstoreUserDetails) curUser).getContact().getUserId()).size();
        }
        else {
            return 0;
        }
    }

    @ModelAttribute("countBooksCartAnyUser")
    public Integer getCountBooksCartAnyUser(@CookieValue(name = "cartContents", required = false) String cartContents){
        Integer count = cookieComponent.countBooksCookie(cartContents);
        return (count == 0) ? 0 : count;
    }

    @ModelAttribute("countPostponedBooksAnyUser")
    public Integer getCountBooksPostponedAnyUser(@CookieValue(name = "postponedBook", required = false) String postponedBook){
        Integer count = cookieComponent.countBooksCookie(postponedBook);
        return (count == 0) ? 0 : count;
    }

    @ModelAttribute("countBooksPostponed")
    public Integer getCountBooksPostponed() {
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            return bookService.getListPostponedBooks(((BookstoreUserDetails) curUser).getContact().getUserId()).size();
        } else {
            return 0;
        }
    }

    @ModelAttribute("accountMoney")
    public Double getAccountMoney(@AuthenticationPrincipal BookstoreUserDetails user){
        if (nonNull(user)) {
            Double balanceAccount = user.getContact().getUserId().getBalance();
            return balanceAccount == 0 ? 0.0 : balanceAccount;
        }
        return 0.0;
    }
}
