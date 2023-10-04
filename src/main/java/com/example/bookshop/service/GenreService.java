package com.example.bookshop.service;

import com.example.bookshop.repository.GenreRepository;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface GenreService {

    Map<GenreType, List<GenreEntity>> getGenresMap();

}
