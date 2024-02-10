package com.example.bookshop.controllers.user;

import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.security.exception.UserNotFoundException;
import com.example.bookshop.service.BookService;

import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
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

import java.util.*;

@Controller
@RequestMapping("/api/books")
@Slf4j
public class BookShopCartController {

    public static final String CART = "CART";

    public static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_CART = "redirect:/api/books/cart";

    private final BookService bookService;
    private final BookstoreUserRegister userRegister;
    @Qualifier("cookieService")
    private final CookieService cookieService;
    private final PaymentService paymentService;

    @Autowired
    public BookShopCartController(BookService bookService,
                                  BookstoreUserRegister userRegister,
                                  CookieService cookieService,
                                  PaymentService paymentService) {
        this.bookService = bookService;
        this.userRegister = userRegister;
        this.cookieService = cookieService;
        this.paymentService = paymentService;
    }

    @GetMapping("/cart")
    public String handlerCartRequest(@CookieValue(name = "cartContents", required = false) String cartContents,
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
            cookieService.priceCalculatorForCookie(cartContents, model, CART);
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
    public String handlerRemoveBookFromCartRequest(@PathVariable("slug") String slug, @CookieValue(name = "cartContents", required = false) String cartContents, HttpServletResponse response, HttpServletRequest request, Model model) {
        Object curUser = userRegister.getCurrentUser();
        BookEntity book = bookService.getBookPageSlug(slug);
        if (curUser instanceof BookstoreUserDetails) {
            bookService.removeBook2User(book, ((BookstoreUserDetails) curUser).getContact().getUserId());
            bookService.updateCountBooksCart(slug, book.getQuantityTheBasket() - 1);
        } else{
            cookieService.deleteBookFromCookieCart(slug, cartContents, response, model);
            bookService.updateCountBooksCart(slug, book.getQuantityTheBasket() - 1);
           cookieService.clearCookie(response, request);
        }
        return REDIRECT_CART;
    }

    @GetMapping("/pay")
    public String handlerPay(Model model) {
        Object user = userRegister.getCurrentUser();
        if (user instanceof BookstoreUserDetails) {
            List<BookEntity> bookList = bookService.getBooksCart(((BookstoreUserDetails) user).getContact().getUserId());
            Double allSumBooks = bookList.stream().mapToDouble(BookEntity::discountPrice).sum();
            Double accountMoney = (Double) ((BindingAwareModelMap) model).get("accountMoney");
            if (accountMoney < allSumBooks) {
                return REDIRECT_CART + "?noMoney=true";
            }
            paymentService.countingAndSavingPurchases(bookList, allSumBooks, (BookstoreUserDetails) user, model);
        }
        return REDIRECT_CART;
    }

    @PostMapping("/changeBookStatus/archived/{slug}")
    public String handlerChangeBookArhived(@PathVariable(name = "slug") String slug,
                                          @AuthenticationPrincipal BookstoreUserDetails user){

        BookEntity book = bookService.getBookPageSlug(slug);
        bookService.saveBookUser(book, user.getContact().getUserId(), Book2UserTypeEntity.StatusBookType.ARCHIVED);

        return REDIRECT_SLUG + slug;
    }
}

