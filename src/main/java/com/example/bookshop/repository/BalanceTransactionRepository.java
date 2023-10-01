package com.example.bookshop.repository;

import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, Integer> {

    List<BalanceTransactionEntity> findBalanceTransactionEntitiesByUserId(UserEntity userId);
}
