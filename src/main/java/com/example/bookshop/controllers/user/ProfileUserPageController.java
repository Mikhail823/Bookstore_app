package com.example.bookshop.controllers.user;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.dto.TransactionPageDto;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.MailException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;


@Controller
@Slf4j
public class ProfileUserPageController {

    private static final String PROF_REDIRECT_PAY = "http://192.168.1.3:8081/profile";
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
    public PaymentDto getPayment() {
        return new PaymentDto();
    }

    @GetMapping("/my")
    public String handleMy(@AuthenticationPrincipal BookstoreUserDetails user, Model model) {
        model.addAttribute("myBooks", bookService.getNotReadBooks(user.getContact().getUserId().getId()));
        model.addAttribute("curUsr", user.getContact().getUserId());
        return "my";
    }

    @GetMapping("/profile")
    public String handlerProfile(@AuthenticationPrincipal BookstoreUserDetails user,
                                 @RequestParam(value = "successfulSave", required = false) Boolean successfulSave,
                                 @RequestParam(value = "sendMsgMail", required = false) Boolean save, Model model) {

        if (Boolean.TRUE.equals(successfulSave))
            model.addAttribute("successfulSave", true);
        if (Boolean.TRUE.equals(save)) {
            model.addAttribute("sendMsgMail", true);
        } else {
            model.addAttribute("sendMsgMail", false);
        }
        UserContactEntity email = userService.findContactUser(user.getContact().getUserId(), ContactType.EMAIL);
        UserContactEntity phone = userService.findContactUser(user.getContact().getUserId(), ContactType.PHONE);
        model.addAttribute("transactionList",
                paymentService.getPageTransactionalUser(user.getContact().getUserId(), 0, 6));
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

    @SneakyThrows
    @PostMapping("/profile/save")
    public String updateProfile(@RequestParam("phone") String phone,
                                @RequestParam("mail") String email,
                                @RequestParam("name") String name,
                                @RequestParam("password") String password,
                                @RequestParam("passwordReply") String passwordReply, Model model) throws JsonProcessingException {

        userService.checkPassword(password, passwordReply, model);
        try {
            userService.confirmChangingUserProfile(phone, email, name, passwordReply);
        } catch (MailException ex) {
            return PROF_REDIRECT + "?sendMsgMail=false";
        }
        return PROF_REDIRECT + "?sendMsgMail=true";
    }

    @PostMapping(value = "/payment")
    public String handlerPayment(@RequestBody PaymentDto pay) throws NoSuchAlgorithmException {
        UserEntity user = ((BookstoreUserDetails) userRegister.getCurrentUser()).getContact().getUserId();
        paymentService.saveTransactionUserBalance(user, pay);
        return PROF_REDIRECT;
    }

    @GetMapping("/profile/verify/{token}")
    public String handleProfileVerification(@PathVariable String token) throws JsonProcessingException {
        userService.changeUserProfile(token);
        return PROF_REDIRECT + "?successfulSave=true";
    }

    @GetMapping("/payment/success")
    public RedirectView successURLHandler(@RequestParam(value = "OutSum", required = false) Double outSum,
                                          @RequestParam(value = "InvId", required = false) Integer invId,
                                          @RequestParam("SignatureValue") String signatureValue,
                                          @RequestParam("IsTest") String isTest,
                                          @RequestParam("Culture") String culture) {


        String description = "Пополнение счета через сервис ROBOKASSA на сумму " + outSum + " руб.";
        paymentService.savingTransaction(signatureValue, outSum, invId, description);
        return new RedirectView(PROF_REDIRECT_PAY);
    }

    @GetMapping("/payment/result")
    public RedirectView resultUrlHandler(@RequestParam("OutSum") Double outSum,
                                         @RequestParam("InvId") Integer invId,
                                         @RequestParam("SignatureValue") String signatureValue,
                                         @RequestParam("Culture") String culture,
                                         @RequestParam("IsTest") String isTest, Model model) {


        String description = "Пополнение счета через сервис ROBOKASSA на сумму " + outSum + " руб.";
        paymentService.savingTransaction(signatureValue, outSum, invId, description);
        model.addAttribute("res", true);
        model.addAttribute("paymentResult", "Денежные средства зачислены на счет!");

        return new RedirectView(PROF_REDIRECT_PAY);

    }

    @GetMapping("/payment/failPayment")
    public RedirectView failPaymentUrlHandler(@RequestParam("OutSum") Double outSum,
                                              @RequestParam("InvId") Integer invId,
                                              @RequestParam("Culture") String culture, Model model) {
        model.addAttribute("error", "Ошибка зачисления денежных средств!!!!!:((");
        return new RedirectView(PROF_REDIRECT_PAY);
    }

    @GetMapping("/transactions")
    @ResponseBody
    public TransactionPageDto handlerPageTransactional(@RequestParam(value = "sort") String sort,
                                                        @RequestParam(value = "offset") Integer offset,
                                                       @RequestParam(value = "limit") Integer limit) {
        UserEntity user = ((BookstoreUserDetails) userRegister.getCurrentUser()).getContact().getUserId();
        return new TransactionPageDto(paymentService.getPageTransactionalUser(user, offset, limit).getContent());
    }


}
