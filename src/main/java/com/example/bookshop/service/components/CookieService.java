package com.example.bookshop.service.components;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


public interface CookieService {
    void addBooksCookieCart(String cookies, Model model,
                                   Map<String, String> allParams, HttpServletResponse response, String slug);

    void addBooksCookiePostponed(String cookies, Model model,
                                        Map<String, String> allParams, HttpServletResponse response, String slug);

    void addUserCookie(HttpServletResponse response, HttpServletRequest request);

    void clearCookie(HttpServletResponse response, HttpServletRequest request);

    String[] splitCookie(String contents);

    List<String> slugsCookieCart(String contents);

    void cookieCartBooks(String contents, Model model);

    void cookiePostponedBooks(String contents, Model model);

    void deleteBookFromCookieCart(String slug, String cartContents, HttpServletResponse response, Model model);

    void deleteBookThePostponedCookie(String slug, String postponedBook, HttpServletResponse response, Model model);

    Integer countBooksCookie(String cartContents);

    List<String> getListBooksAnonymousUser(String cartContents);

    String getHashTheUserFromCookie(HttpServletRequest request);
}
