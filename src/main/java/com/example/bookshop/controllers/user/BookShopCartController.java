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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class BookShopCartController {

    @Autowired
    private final BookService bookService;
    @Autowired
    private final BookstoreUserRegister userRegister;
    @Qualifier("cookieService")
    private final CookieService cookieService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final PaymentService paymentService;
    public static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_CART = "redirect:/api/books/cart";

    @GetMapping("/cart")
    public String handleCartRequest(@CookieValue(name = "cartContents", required = false) String cartContents,
                                    @RequestParam(name = "noMoney", required = false) Boolean noMoney,
                                    Model model, HttpServletResponse response,
                                    HttpServletRequest request) {
        Object curUser = userRegister.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            if (noMoney == null) {
                model.addAttribute("noMoney", "На Вашем балансе не достаточно средств!!!");
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

        Object curUser = userRegister.getCurrentUser();
        BookEntity book = bookService.getBookPageSlug(slug);
        int countCartBooks = book.getQuantityTheBasket() == null ? 0 : book.getQuantityTheBasket();
        if (curUser instanceof BookstoreUserDetails) {
            UserEntity user = userService.getUserName(((BookstoreUserDetails) curUser).getContact().getUserId().getName());
                bookService.saveBook2User(book, user, CART);
                bookService.updateCountBooksCart(slug, countCartBooks + 1);
                return REDIRECT_SLUG + slug;
        } else {
            cookieService.addBooksCookieCart(cartContents, model, allParams, response, slug);
            bookService.updateCountBooksCart(slug, countCartBooks + 1);
        }
        return REDIRECT_SLUG + slug;
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
        return "redirect:/cart";
    }

    @GetMapping("/pay")
    public String handlerPay(@AuthenticationPrincipal BookstoreUserDetails user, Model model) {

        List<BookEntity> bookList = bookService.getBooksCart(user.getContact().getUserId());
        Double allSumBooks = bookList.stream().mapToDouble(BookEntity::discountPrice).sum();
        Double accountMoney = (Double) ((BindingAwareModelMap) model).get("accountMoney");
        if (accountMoney < allSumBooks) {
            return REDIRECT_CART + "?noMoney=true";
        }
        for (BookEntity b : bookList) {
            int countPayBooks = b.getNumberOfPurchased() == null ? 0 : b.getNumberOfPurchased() + 1;
            bookService.updateCountPaidBooks(b.getSlug(), countPayBooks);
        }

        String bookStrong = (bookList.size() == 1) ? "книги: " : "книг: ";
        String booksName = bookList.stream().map(book -> book.getTitle() + ", ").collect(Collectors.joining());
        Double balance = user.getContact().getUserId().getBalance() - allSumBooks;
        user.getContact().getUserId().setBalance(balance);
        userService.saveUserEntity(user.getContact().getUserId());
        model.addAttribute("accountMoney", balance);
        bookList.forEach(b -> bookService.saveBook2User(b, user.getContact().getUserId(), PAID));
        BalanceTransactionEntity transaction = new BalanceTransactionEntity();
        transaction.setUserId(user.getContact().getUserId());
        transaction.setDescription("Покупка " + bookStrong + booksName);
        transaction.setValue(allSumBooks);
        transaction.setTime(LocalDateTime.now());
        paymentService.saveTransaction(transaction);
        return "redirect:/api/books/cart";
    }
}

