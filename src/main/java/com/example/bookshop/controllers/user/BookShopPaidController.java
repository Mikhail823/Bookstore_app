package com.example.bookshop.controllers.user;

import com.example.bookshop.security.BookstoreUserDetails;

import com.example.bookshop.service.BookService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookShopPaidController {
    private static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_PROFILE = "http://localhost:8081/profile";

    @Autowired
    private final BookService bookService;
    @Autowired
    private final CookieService cookieService;
    @Autowired
    private final PaymentService paymentService;


    @GetMapping("/success")
    public RedirectView successURLHandler(@RequestParam("OutSum") Double OutSum,
                                          @RequestParam("InvId") Integer InvId,
                                          @RequestParam("SignatureValue") String SignatureValue,
                                          @RequestParam("Culture") String Culture,
                                          @RequestParam("IsTest") String IsTest) throws NoSuchAlgorithmException {


        String description = "Пополнение счета через сервис ROBOKASSA на сумму " + OutSum + " руб.";
        paymentService.savingTransaction(Double.valueOf(OutSum), InvId, description);
        return new RedirectView(REDIRECT_PROFILE);
    }

    @GetMapping("/result")
    public RedirectView resultUrlHandler(@RequestParam("OutSum") Double OutSum,
                                         @RequestParam("InvId") Integer InvId,
                                         @RequestParam("SignatureValue") String SignatureValue,
                                         @RequestParam("Culture") String Culture,
                                         @RequestParam("IsTest") String IsTest, Model model) throws NoSuchAlgorithmException {
        if(paymentService.isSignature(SignatureValue, OutSum, InvId)){
            model.addAttribute("res", true);
            model.addAttribute("paymentResult", "Денежные средства зачислены на счет!");
        }
        return new RedirectView(REDIRECT_PROFILE);

    }

    @GetMapping("/failPayment")
    public RedirectView failPaymentUrlHandler(@RequestParam("OutSum") Double OutSum,
                                              @RequestParam("InvId") Integer InvId,
                                              @RequestParam("Culture") String Culture, Model model){
        model.addAttribute("error", "Ошибка зачисления денежных средств!!!!!:((");
        return new RedirectView(REDIRECT_PROFILE);
    }

    @PostMapping("/changeBookStatus/archived/{slug}")
    public String handleChangeBookArhived(@PathVariable(name = "slug") String slug,
                                          @AuthenticationPrincipal BookstoreUserDetails user){

        BookEntity book = bookService.getBookPageSlug(slug);
        bookService.saveBook2User(book, user.getContact().getUserId(), Book2UserTypeEntity.StatusBookType.ARCHIVED);

        return REDIRECT_SLUG + slug;
    }
}
