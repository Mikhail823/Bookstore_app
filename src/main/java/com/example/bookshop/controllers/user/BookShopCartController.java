package com.example.bookshop.controllers.user;

import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;

import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.bookshop.struct.book.links.Book2UserTypeEntity.StatusBookType.CART;
import static com.example.bookshop.struct.book.links.Book2UserTypeEntity.StatusBookType.PAID;

@Controller
@RequestMapping("/api/books")
@Slf4j
public class BookShopCartController {

    public static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_CART = "redirect:/api/books/cart";

    private final BookService bookService;
    private final BookstoreUserRegister userRegister;
    @Qualifier("cookieService")
    private final CookieService cookieService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired
    public BookShopCartController(BookService bookService,
                                  BookstoreUserRegister userRegister,
                                  CookieService cookieService,
                                  UserService userService,
                                  PaymentService paymentService) {
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.cookieService = cookieService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(name = "cartContents", required = false) String cartContents,
                                    @RequestParam(name = "noMoney", required = false) Boolean noMoney,
                                    Model model, HttpServletResponse response,
                                    HttpServletRequest request) {
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            if (Boolean.TRUE.equals(noMoney)) {
                model.addAttribute("noMoney", true);
            }
            cookieService.clearCookie(response, request);
            bookService.getBooksTheCartOfUser(model);

        } else {
            cookieService.cookieCartBooks(cartContents, model);
            int totalPrice = 0;
            int oldPrice = 0;
            if (cookieService.getListBooksAnonymousUser(cartContents) == null) {
                model.addAttribute("isCartEmpty", true);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("oldPrice", oldPrice);
            }
            for (String slug : cookieService.getListBooksAnonymousUser(cartContents)) {
                BookEntity book = bookService.getBookPageSlug(slug);
                totalPrice = totalPrice + book.discountPrice();
                oldPrice = oldPrice + book.getPriceOld();
            }
        }
        return "cart";
    }

    @ModelAttribute(name = "bookCart")
    public List<BookEntity> bookCart() {
        return new ArrayList<>();
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(@PathVariable(value = "slug") String slug,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @RequestBody Map<String, String> allParams, Model model, HttpServletResponse response) {

        return bookService.addingBookStatusCart(slug, model, response, cartContents, REDIRECT_CART, allParams);
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String handleRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents, HttpServletResponse response, HttpServletRequest request, Model model) {
        Object curUser = userRegister.getCurrentUser();
        BookEntity book = bookService.getBookPageSlug(slug);
        if (curUser instanceof BookstoreUserDetails) {
            bookService.removeBook2User(book, ((BookstoreUserDetails) curUser).getContact().getUserId());
            bookService.updateCountBooksCart(slug, book.getQuantityTheBasket() - 1);
        } else {
            cookieService.deleteBookFromCookieCart(slug, cartContents, response, model);
            bookService.updateCountBooksCart(slug, book.getQuantityTheBasket() - 1);
            cookieService.clearCookie(response, request);
        }
        return REDIRECT_CART;
    }

    @GetMapping("/pay")
    public String handlerPay(@AuthenticationPrincipal BookstoreUserDetails user, Model model) {

        List<BookEntity> bookList = bookService.getBooksCart(user.getContact().getUserId());
        Double allSumBooks = bookList.stream().mapToDouble(BookEntity::discountPrice).sum();
        Double accountMoney = (Double) ((BindingAwareModelMap) model).get("accountMoney");
        if (accountMoney < allSumBooks) {
            return REDIRECT_CART + "?noMoney=true";
        }
        paymentService.countingAndSavingPurchases(bookList, allSumBooks, user, model);
        return REDIRECT_CART;
    }
}

