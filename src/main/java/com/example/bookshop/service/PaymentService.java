package com.example.bookshop.service;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface PaymentService {

    String getPaymentUrl(UserEntity user, PaymentDto paymentDto) throws NoSuchAlgorithmException;

    void savingTransaction(String signatureValue, Double outSum, Integer invId, String description);

    List<BalanceTransactionEntity> getListTransactionUser(UserEntity user);

    void saveTransaction(BalanceTransactionEntity transactionEntity);

    Page<BalanceTransactionEntity> getPageTransactionalUser(UserEntity user, Integer offset, Integer limit);

    void countingAndSavingPurchases(List<BookEntity> bookList, Double allSumBooks, BookstoreUserDetails user, Model model);

    void saveTransactionUserBalance(UserEntity user, PaymentDto paymentDto);
}
