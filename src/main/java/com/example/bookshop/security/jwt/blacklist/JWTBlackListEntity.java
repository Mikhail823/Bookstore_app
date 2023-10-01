package com.example.bookshop.security.jwt.blacklist;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jwt_black_list")
@Setter
@Getter
public class JWTBlackListEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jwt_token", length = 600)
    private String jwtToken;
}
