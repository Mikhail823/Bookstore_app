package com.example.bookshop.struct.user;

import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.review.BookReviewEntity;
import com.example.bookshop.struct.book.review.MessageEntity;
import com.example.bookshop.struct.payments.BalanceTransactionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

import java.util.*;

@Entity
@Table(name = "users")
@Setter
@Getter

public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private Date regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private double balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user2role" ,
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    @JsonIgnore
    private Set<RoleEntity> roles;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private transient List<MessageEntity> listMessage = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @Transient
    private List<UserContactEntity> listContact = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<BookReviewEntity> review = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "book2user" ,
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")},
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private List<BookEntity> listBooks = new ArrayList<>();

    @OneToMany
    @JsonIgnore
    private List<BalanceTransactionEntity> transaction = new ArrayList<>();

}
