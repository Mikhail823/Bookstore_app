package com.example.bookshop.struct.book.links;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "book2user_type")
@Setter
@Getter
@NoArgsConstructor
public class Book2UserTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusBookType code;

    private String name;

    @AllArgsConstructor
    @Getter
    public enum StatusBookType {
        KEPT ("Отложена"),
        CART ("В корзине"),
        PAID ("Куплена"),
        ARCHIVED ("В архиве"),
        UNLINK ("UNLINK"),
        VIEWED("Просмотренная");

    private final String typeStatus;

    public String getTypeStatus() {
        return typeStatus;
    }
    @Override
    public String toString() {
        return "Статус книги - " + typeStatus;
    }
    }

}
