package com.example.bookshop.service;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;

import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface PaymentService {
    String getPaymentUrl(UserEntity user, PaymentDto paymentDto) throws NoSuchAlgorithmException;
    void savingTransaction(String signatureValue, Double outSum, Integer invId, String description) throws NoSuchAlgorithmException;
    boolean isSignature(String signatureValue, Double sum, Integer invId) throws NoSuchAlgorithmException;
    List<BalanceTransactionEntity> getListTransactionUser(UserEntity user);
    void saveTransaction(BalanceTransactionEntity transactionEntity);
}
