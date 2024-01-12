package com.example.bookshop.controllers.user;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.dto.ProfileFormDto;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

import java.util.List;

@Controller
@Slf4j
public class ProfileUserPageController {

    private static final String PROF_REDIRECT = "redirect:/profile";
    private static final String PROFILE = "profile";
    private final UserService userService;
    private final PaymentService paymentService;
    private final BookService bookService;
    private final BookstoreUserRegister userRegister;

    @Autowired
    public ProfileUserPageController(UserService userService,
                                     PaymentService paymentService,
                                     BookService bookService,
                                     BookstoreUserRegister userRegister) {

        this.userService = userService;
        this.paymentService = paymentService;
        this.bookService = bookService;
        this.userRegister = userRegister;
    }
    @ModelAttribute("paymentDto")
    public PaymentDto getPayment(){
        return new PaymentDto();
    }


    @GetMapping("/my")
    public String handleMy(@AuthenticationPrincipal BookstoreUserDetails user, Model model) {
        model.addAttribute("myBooks", bookService.getNotReadBooks(user.getContact().getUserId().getId()));
        model.addAttribute("curUsr", user.getContact().getUserId());
        return "my";
    }

    @GetMapping("/profile")
    public String handlerProfile(@AuthenticationPrincipal BookstoreUserDetails user, Model model) {
        List<BalanceTransactionEntity> listTransaction =
                paymentService.getListTransactionUser(user.getContact().getUserId());
        UserContactEntity email = userService.findContactUser(user.getContact().getUserId(), ContactType.EMAIL);
        UserContactEntity phone = userService.findContactUser(user.getContact().getUserId(), ContactType.PHONE);
        model.addAttribute("transactionList", listTransaction);
        model.addAttribute("email", email.getContact());
        model.addAttribute("phone", phone.getContact());
        model.addAttribute("curUsr", user.getContact().getUserId());

        return PROFILE;
    }

    @GetMapping("/archive")
    public String handlerArchive(Model model, @AuthenticationPrincipal BookstoreUserDetails user) {
        model.addAttribute("myArchiveBooks", bookService.getBooksArchiveUser(user.getContact().getUserId().getId()));
        return "myarchive";
    }

    @PostMapping("/profile/save")
    public String updateProfile(ProfileFormDto profileDto) throws JsonProcessingException {
        userService.confirmChangingUserProfile(profileDto);
        return PROF_REDIRECT;
    }

    @PostMapping("/payment")
    public RedirectView handlerPayment(@RequestBody PaymentDto paymentDto) throws NoSuchAlgorithmException {
    log.info("SUUUUM _____________________ " + paymentDto.getSum());
        UserEntity user = ((BookstoreUserDetails) userRegister.getCurrentUser()).getContact().getUserId();
        String redirectUrl = paymentService.getPaymentUrl(user, paymentDto);
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/profile/verify/{token}")
    public String handleProfileVerification(@PathVariable String token) throws JsonProcessingException {
        userService.changeUserProfile(token);
        return PROF_REDIRECT;
    }

    @GetMapping("/payment/success")
    public RedirectView successURLHandler(@RequestParam(value = "outSum", required = false) Double outSum,
                                          @RequestParam("invId") Integer invId,
                                          @RequestParam("signatureValue") String signatureValue,
                                          @RequestParam("Culture") String culture,
                                          @RequestParam("IsTest") String isTest) throws NoSuchAlgorithmException {

        String description = "Пополнение счета через сервис ROBOKASSA на сумму " + outSum + " руб.";
        paymentService.savingTransaction(signatureValue, outSum, invId, description);
        return new RedirectView(PROF_REDIRECT);
    }

    @GetMapping("/payment/result")
    public RedirectView resultUrlHandler(@RequestParam("OutSum") Double outSum,
                                         @RequestParam("invId") Integer invId,
                                         @RequestParam("SignatureValue") String signatureValue,
                                         @RequestParam("Culture") String culture,
                                         @RequestParam("IsTest") String isTest, Model model) throws NoSuchAlgorithmException {
        if(paymentService.isSignature(signatureValue, outSum, invId)){
            model.addAttribute("res", true);
            model.addAttribute("paymentResult", "Денежные средства зачислены на счет!");
        }
        return new RedirectView(PROF_REDIRECT);

    }

    @GetMapping("/payment/failPayment")
    public RedirectView failPaymentUrlHandler(@RequestParam("OutSum") Double outSum,
                                              @RequestParam("InvId") Integer invId,
                                              @RequestParam("Culture") String culture, Model model){
        model.addAttribute("error", "Ошибка зачисления денежных средств!!!!!:((");
        return new RedirectView(PROF_REDIRECT);
    }


}
