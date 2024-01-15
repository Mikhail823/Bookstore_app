package com.example.bookshop.dto;

import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransactionPageDto {

    private Integer count;
    private List<BalanceTransactionEntity> transactions;

    public TransactionPageDto(List<BalanceTransactionEntity> transactions){
        this.transactions = transactions;
        this.count = transactions.size();

    }

}
