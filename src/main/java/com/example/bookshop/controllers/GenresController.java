package com.example.bookshop.controllers;

import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.GenreService;
import com.example.bookshop.struct.enums.GenreType;
import com.example.bookshop.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class GenresController {

    private final GenreService genreService;
    private final BookService bookService;

    @Autowired
    public GenresController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @GetMapping("/genres")
    public String genresPage(Model model)
    {
        model.addAttribute("genres", genreService.getGenresMap());
        return "/genres/index";
    }

    @GetMapping("/genres/{genre}")
    public String getGenrePage(@PathVariable(value = "genre") GenreEntity genre,
                                   Model model){
        model.addAttribute("gen", genre);
        model.addAttribute("books", bookService.getBooksOfGenre(genre, 0, 5).getContent());

        return "/genres/slug";
    }

    @GetMapping("/genres/type{genreType}")
    public String getGenreTypePage(@PathVariable(value = "genreType")GenreType type,
                                   Model model){
        model.addAttribute("type", type);
        model.addAttribute("booksOfType", bookService.getBooksOfGenreType(type, 0, 5).getContent());
        return "/genres/slug_type";
    }
    @GetMapping("/books/genre/{id}")
    @ResponseBody
    public BooksPageDto getNextPage(@PathVariable(value = "id") GenreEntity genreId,
                                    @RequestParam(value = "offset") Integer offset,
                                    @RequestParam(value = "limit") Integer limit){
        return new BooksPageDto(bookService.getBooksOfGenre(genreId, offset, limit).getContent());
    }

    @GetMapping("/books/genreType/{types}")
    @ResponseBody
    public BooksPageDto getGenreTypeBooks(@PathVariable(value = "types") String type,
                                          Integer offset, Integer limit){
        return new BooksPageDto(bookService.getBooksOfGenreType(GenreType.valueOf(type), offset, limit).getContent());
    }
    

}
