package com.example.bookshop.service;

import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;

import java.security.NoSuchAlgorithmException;
import java.util.List;


public interface PaymentService {
    String getPaymentUrl(UserEntity user, Double sum) throws NoSuchAlgorithmException;
    void savingTransaction(Double outSum, Integer invId, String description);
    boolean isSignature(String signatureValue, Double sum, Integer invId) throws NoSuchAlgorithmException;
    List<BalanceTransactionEntity> getListTransactionUser(UserEntity user);
    void saveTransaction(BalanceTransactionEntity transactionEntity);
}
