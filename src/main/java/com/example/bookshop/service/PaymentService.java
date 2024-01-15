package com.example.bookshop.service;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;

import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface PaymentService {
    String getPaymentUrl(UserEntity user, PaymentDto paymentDto) throws NoSuchAlgorithmException;
    void savingTransaction(String signatureValue, Double outSum, Integer invId, String description);
    List<BalanceTransactionEntity> getListTransactionUser(UserEntity user);
    void saveTransaction(BalanceTransactionEntity transactionEntity);
    Page getPageTransactionalUser(UserEntity user, Integer offset, Integer limit);
}
