package com.example.bookshop.service.impl;

import com.example.bookshop.repository.GenreRepository;
import com.example.bookshop.service.GenreService;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Map<GenreType, List<GenreEntity>> getGenresMap(){
        List<GenreEntity> genresList = genreRepository.findAll();
        if (!genresList.isEmpty()) {
            return genresList.stream().collect(Collectors.groupingBy(GenreEntity::getParentId));
        }
        return new EnumMap<>(GenreType.class);
    }

    @Override
    public List<GenreEntity> getAllGenres() {
        return genreRepository.findAll();
    }


}
