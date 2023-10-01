package com.example.bookshop.struct.enums;

public enum GenreType {
    EASY_READING("Легкое чтение"),
    SERIOUS_READING("Серьёзное чтение"),
    BUSINESS_LITERATURE("Деловая литература"),
    DRAMA("Драматургия");

    public final String typeGenre;

    GenreType(String typeGenre) {
        this.typeGenre = typeGenre;
    }

    public String getTypeGenre() {
        return typeGenre;
    }

    @Override
    public String toString() {
        return typeGenre;
    }
}
