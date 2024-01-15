package com.example.bookshop.struct.user;
import com.example.bookshop.struct.enums.ContactType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_contact")
@Setter
@Getter
public class UserContactEntity implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userId;

    @Enumerated(EnumType.STRING)
    private ContactType type;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private short approved;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String code;

    @Column(name = "code_trails", columnDefinition = "INT")
    private int codeTrails;

    @Column(name = "code_time",columnDefinition = "TIMESTAMP")
    private LocalDateTime codeTime;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String contact;

    public UserContactEntity(){}

    public UserContactEntity(String code, Integer expireIn) {
        this.code = code;
        this.codeTime = LocalDateTime.now().plusSeconds(expireIn);
    }
}
