package com.example.bookshop.controllers.user;

import com.example.bookshop.security.BookstoreUserDetails;

import com.example.bookshop.service.BookService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/api/books")
public class BookShopPaidController {

    private static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_PROFILE = "http://localhost:8081/profile";

    private final BookService bookService;
    private final PaymentService paymentService;

    @Autowired
    public BookShopPaidController(BookService bookService, PaymentService paymentService) {
        this.bookService = bookService;
        this.paymentService = paymentService;
    }

    @GetMapping("/success")
    public RedirectView successURLHandler(@RequestParam("outSum") Double outSum,
                                          @RequestParam("invId") Integer invId,
                                          @RequestParam("signatureValue") String signatureValue,
                                          @RequestParam("Culture") String culture,
                                          @RequestParam("IsTest") String isTest) {

        String description = "Пополнение счета через сервис ROBOKASSA на сумму " + outSum + " руб.";
        paymentService.savingTransaction(outSum, invId, description);
        return new RedirectView(REDIRECT_PROFILE);
    }

    @GetMapping("/result")
    public RedirectView resultUrlHandler(@RequestParam("OutSum") Double outSum,
                                         @RequestParam("invId") Integer invId,
                                         @RequestParam("SignatureValue") String signatureValue,
                                         @RequestParam("Culture") String culture,
                                         @RequestParam("IsTest") String isTest, Model model) throws NoSuchAlgorithmException {
        if(paymentService.isSignature(signatureValue, outSum, invId)){
            model.addAttribute("res", true);
            model.addAttribute("paymentResult", "Денежные средства зачислены на счет!");
        }
        return new RedirectView(REDIRECT_PROFILE);

    }

    @GetMapping("/failPayment")
    public RedirectView failPaymentUrlHandler(@RequestParam("OutSum") Double outSum,
                                              @RequestParam("InvId") Integer invId,
                                              @RequestParam("Culture") String culture, Model model){
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
