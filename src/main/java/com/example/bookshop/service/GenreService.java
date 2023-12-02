package com.example.bookshop.service;

import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;

import java.util.List;
import java.util.Map;


public interface GenreService {

    Map<GenreType, List<GenreEntity>> getGenresMap();
    List<GenreEntity> getAllGenres();

}
