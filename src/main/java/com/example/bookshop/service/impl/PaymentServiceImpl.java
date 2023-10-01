package com.example.bookshop.service.impl;

import com.example.bookshop.repository.BalanceTransactionRepository;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.struct.enums.PaymentStatusType;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass}")
    private String firstTestPass;

    @Value("${robokassa.pass2}")
    private String towTestPass;

    @Autowired
    private final BalanceTransactionRepository balanceTransactionRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserContactRepository contactRepository;

    @Override
    public String getPaymentUrl(UserEntity user, Double sum) throws NoSuchAlgorithmException {
        List<UserContactEntity> userContactList = contactRepository.findUserContactEntitiesByUserId(user);
        String email = "";
        for (UserContactEntity contact : userContactList){
            if (contact.getContact().contains("@")){
                email = contact.getContact();
            }
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        Integer invId = user.getId();
        md.update((merchantLogin + ":" + sum.toString() + ":"
                + invId + ":" + towTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin + "&Pass1=" + firstTestPass +
                "&InvId=" + invId +
                "&OutSum=" + sum.toString() +
                "&Description=" + "Buying books" +
                "&Email=" + email +
                "&Culture=ru"+
                "&Encoding=utf-8"+
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }
    @Override
    public boolean isSignature(String signatureValue, Double sum, Integer invId) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + sum.toString() + ":"
                + invId + ":" + towTestPass).getBytes());
        signatureValue = DatatypeConverter.printHexBinary(signatureValue.getBytes()).toUpperCase();
        String value = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
        if (signatureValue.equals(value)) {
        return true;
        }
        return false;
    }


    @Transactional
    @Override
    public void savingTransaction(Double outSum, Integer invId, String description){
        UserEntity user = userRepository.findUserEntityById(invId);
        Double allSum = user.getBalance() + outSum;
        userRepository.updateUserBalance(allSum, user.getId());
        BalanceTransactionEntity transactionEntity = new BalanceTransactionEntity();
        transactionEntity.setValue(outSum);
        transactionEntity.setDescription(description);
        transactionEntity.setPaymentStatus(PaymentStatusType.OK);
        transactionEntity.setTime(LocalDateTime.now());
        transactionEntity.setUserId(user);
        balanceTransactionRepository.save(transactionEntity);
    }

    @Override
    public List<BalanceTransactionEntity> getListTransactionUser(UserEntity user){
        return balanceTransactionRepository.findBalanceTransactionEntitiesByUserId(user);
    }

    @Override
    @Transactional
    public void saveTransaction(BalanceTransactionEntity transactionEntity) {
        balanceTransactionRepository.save(transactionEntity);
    }
}
