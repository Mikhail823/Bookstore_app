package com.example.bookshop.struct.payments;

import com.example.bookshop.struct.enums.PaymentStatusType;
import com.example.bookshop.struct.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance_transaction")
@Getter
@Setter
public class BalanceTransactionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private Double value;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusType paymentStatus;

}
