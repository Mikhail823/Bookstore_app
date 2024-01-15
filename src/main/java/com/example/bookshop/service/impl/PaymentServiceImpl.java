package com.example.bookshop.service.impl;

import com.example.bookshop.dto.PaymentDto;
import com.example.bookshop.repository.BalanceTransactionRepository;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.service.PaymentService;
import com.example.bookshop.struct.enums.PaymentStatusType;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokassa.pass}")
    private String firstTestPass;

    @Value("${robokassa.pass2}")
    private String towTestPass;

    private final BalanceTransactionRepository balanceTransactionRepository;
    private final UserRepository userRepository;
    private final UserContactRepository contactRepository;

    @Autowired
    public PaymentServiceImpl(BalanceTransactionRepository balanceTransactionRepository,
                              UserRepository userRepository,
                              UserContactRepository contactRepository) {
        this.balanceTransactionRepository = balanceTransactionRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public String getPaymentUrl(UserEntity user,
                                PaymentDto paymentDto) throws NoSuchAlgorithmException {
        List<UserContactEntity> userContactList = contactRepository.findUserContactEntitiesByUserId(user);
        String email = "";
        for (UserContactEntity contact : userContactList){
            if (contact.getContact().contains("@")){
                email = contact.getContact();
            }
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        int invId = user.getId();
        md.update((merchantLogin + ":" + paymentDto.getSum() + ":"
                + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin + "&Pass1=" + firstTestPass +
                "&InvId=" + invId +
                "&OutSum=" + paymentDto.getSum() +
                "&Description=" + "Adding funds to your account" +
                "&Email=" + email +
                "&Culture=ru"+
                "&Encoding=utf-8"+
                "&SignatureValue=" + DatatypeConverter.printHexBinary(md.digest()).toUpperCase() +
                "&IsTest=1";
    }

    @Transactional
    @Override
    public void savingTransaction(String signatureValue,
                                  Double outSum,
                                  Integer invId,
                                  String description){

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

    @Override
    public Page getPageTransactionalUser(UserEntity user, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return balanceTransactionRepository.findBalanceTransactionEntitiesByUserIdOrderByTimeAsc(user, nextPage);
    }
}
