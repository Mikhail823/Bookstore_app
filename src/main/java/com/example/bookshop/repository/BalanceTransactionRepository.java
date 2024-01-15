package com.example.bookshop.repository;

import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, Integer> {

    List<BalanceTransactionEntity> findBalanceTransactionEntitiesByUserId(UserEntity userId);


    Page<BalanceTransactionEntity> findBalanceTransactionEntitiesByUserIdOrderByTimeAsc(UserEntity user, Pageable page);
}
