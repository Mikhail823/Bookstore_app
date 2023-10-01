package com.example.bookshop.struct.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatusType {
    OK("Успешно"),
    FAIL("Ошибка");

    public final String paymentStatus;

    @Override
    public String toString() {
        return paymentStatus;
    }
}


