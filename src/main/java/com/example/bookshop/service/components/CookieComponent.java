package com.example.bookshop.service.components;

import com.example.bookshop.service.BookService;
import com.example.bookshop.service.BooksRatingAndPopulatityService;

import com.example.bookshop.service.UserService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CookieComponent implements CookieService{

    public static final String POST_BOOK = "postponedBook";
    public static final String CART_CONTENT = "cartContents";
    public static final String CART_EMPTY = "isCartEmpty";
    public static final String STATUS = "status";
    public static final String POSTPONED_EMPTY = "isPostponedEmpty";
    public static final String TOTAL_RATING = "totalRating";
    public static final String BOOK_CART = "bookCart";
    public static final String KEPT = "KEPT";
    public static final String CART = "CART";

    private final UserService userServiceImp;
    private final BookService bookService;
    private final BooksRatingAndPopulatityService booksRatingAndPopulatityService;

    @Autowired
    public CookieComponent(UserService userServiceImp, BookService bookService,
                           BooksRatingAndPopulatityService booksRatingAndPopulatityService) {
        this.userServiceImp = userServiceImp;
        this.bookService = bookService;
        this.booksRatingAndPopulatityService = booksRatingAndPopulatityService;
    }

    @Override
    public void addBooksCookieCart(String cookies, Model model,
                               Map<String, String> allParams, HttpServletResponse response, String slug) {
        if (cookies == null || cookies.equals("")) {
            Cookie cookie = new Cookie(CART_CONTENT, allParams.get(STATUS) + "=" + slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(CART_CONTENT, false);
        } else if (!cookies.contains(allParams.get(STATUS) + "=" + slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cookies).add(allParams.get(STATUS) + "=" + slug);
            Cookie cookie = new Cookie(CART_CONTENT, stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(CART_EMPTY, false);
        }
    }

    @Override
    public void addBooksCookiePostponed(String cookies, Model model,
                                        Map<String, String> allParams, HttpServletResponse response, String slug){
        if (cookies == null || cookies.equals("")) {
            Cookie cookie = new Cookie(POST_BOOK, allParams.get(STATUS) + "=" + slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(POSTPONED_EMPTY, false);
        } else if (!cookies.contains(allParams.get(STATUS) + "=" + slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cookies).add(allParams.get(STATUS) + "=" + slug);
            Cookie cookie = new Cookie(POST_BOOK, stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(POSTPONED_EMPTY, false);
        }
    }

    @Override
    public void addUserCookie(HttpServletResponse response, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        if (session.isNew()) {
            UserEntity user = userServiceImp.createAnonymousUser();
            Cookie cookie = new Cookie("USER-ANONYMOUS", user.getHash());
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            for (Cookie cookie : cookies) {
                String userCookie = cookie.getName();
                Cookie cookie1 = new Cookie(userCookie, cookie.getValue());
                cookie1.setPath("/");
                response.addCookie(cookie1);
            }
        }
    }

    @Override
    public void clearCookie(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals(CART_CONTENT) && c.getValue().equals("")
                    || c.getName().equals(POST_BOOK) && c.getValue().equals("")) {
                c.setPath("/");
                c.setMaxAge(0);
                c.setValue("");
                response.addCookie(c);
            }
        }
    }
    @Override
    public String[] splitCookie(String contents) {
        String[] mass = {"notFoundContents"};
        if (contents != null) {
            contents = contents.startsWith("/") ? contents.substring(1) : contents;
            contents = contents.endsWith("/") ? contents.substring(0, contents.length() - 1) : contents;
            return contents.split("/");
        }
        return mass;
    }

    @Override
    public List<String> slugsCookieCart(String contents) {
        return Arrays.stream(splitCookie(contents))
                .filter(s -> s.contains(CART))
                .map(s -> s.replace(CART + "=", ""))
                .collect(Collectors.toList());
    }

    @Override
    public void cookieCartBooks(String contents, Model model) {
        if (contents == null || contents.equals("")) {
            model.addAttribute(CART_EMPTY, true);
        } else {
            model.addAttribute(CART_EMPTY, false);
            List<String> suitableCookieSlugs = slugsCookieCart(contents);
            List<BookEntity> booksFromCookieSlugs = bookService.getBooksBySlugIn(suitableCookieSlugs);
            model.addAttribute(BOOK_CART, booksFromCookieSlugs);
            for (BookEntity book : booksFromCookieSlugs) {
                model.addAttribute(TOTAL_RATING, booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
            }
        }
    }

    @Override
    public void cookiePostponedBooks(String contents, Model model) {
        if (contents == null || contents.equals("")){
            model.addAttribute(POSTPONED_EMPTY, true);
        }
        else {
            model.addAttribute(POSTPONED_EMPTY, false);
            contents = contents.startsWith("/") ? contents.substring(1) : contents;
            contents = contents.endsWith("/") ? contents.substring(0, contents.length() - 1) : contents;
            String[] cookiePostponedBook = contents.split("/");
            List<String> suitableCookieSlugs = Arrays.stream(cookiePostponedBook)
                    .filter(s -> s.contains(KEPT))
                    .map(s -> s.replace(KEPT + "=",""))
                    .collect(Collectors.toList());
            List<BookEntity> booksFromCookieSlug = bookService.getBooksBySlugIn(suitableCookieSlugs);
            for (BookEntity book : booksFromCookieSlug){
                model.addAttribute(TOTAL_RATING, booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
            }
            model.addAttribute(POST_BOOK, booksFromCookieSlug);

        }
    }

    @Override
    public void deleteBookFromCookieCart(String slug, String cartContents, HttpServletResponse response, Model model) {
        if (cartContents.isEmpty()) {
            ArrayList<String> booksCookie = new ArrayList<>
                    (Arrays.asList(cartContents.split("/")));
            booksCookie.remove(CART + "=" + slug);
            Cookie cookie = new Cookie(CART_CONTENT, String.join("/", booksCookie));
            cookie.setPath("/");
            response.addCookie(cookie);

            isCartEmpty(booksCookie, model);
        } else {
            model.addAttribute(CART_EMPTY, true);
        }
    }

    public Model isCartEmpty(List<String> cookie, Model model){
        if (cookie.isEmpty()){  return model.addAttribute(CART_EMPTY, false);}
        return model.addAttribute(CART_EMPTY, true);
    }
    @Override
    public void deleteBookThePostponedCookie(String slug, String postponedBook,
                                             HttpServletResponse response, Model model){
        if (postponedBook != null || !postponedBook.equals("")){
            ArrayList<String> cookieBookPostponedList = new ArrayList<>(Arrays.asList(postponedBook.split("/")));
            cookieBookPostponedList.remove(slug);
            Cookie cookie = new Cookie(POST_BOOK, String.join("/", cookieBookPostponedList));
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute(POSTPONED_EMPTY, false);
        }
        else {
            model.addAttribute(POSTPONED_EMPTY, true);
        }
    }

    @Override
    public Integer countBooksCookie(String cartContents) {
        List<String> listCookie = slugsCookieCart(cartContents);
        if (listCookie != null){
            return listCookie.size();
        }else {
            return 0;
        }
    }

    @Override
    public List<String> getListBooksAnonymousUser(String cartContents) {
        List<String> listBooks = slugsCookieCart(cartContents);
        if (listBooks == null){
            return new ArrayList<>();
        }
        return listBooks;
    }
}
