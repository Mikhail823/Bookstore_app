package com.example.bookshop.controllers;


import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.service.AuthorService;
import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.author.AuthorEntity;
import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api("author data")
@RequestMapping("/api")
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AuthorsController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @ModelAttribute("authorsMap")
    public Map<String,List<AuthorEntity>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage(){
        return "/authors/index";
    }

    @GetMapping("/author/{id:\\d+}")
    public String getPageAuthor(@PathVariable(value = "id") Integer id, Model model){
        Page<BookEntity> booksPage = bookService.getBooksByAuthorId(id, 0, 6);
        model.addAttribute("author", authorService.getAuthorById(id));
        model.addAttribute("authorBooks", booksPage.getContent());
        model.addAttribute("size", booksPage.getTotalElements());
        return "/authors/slug";
    }

    @GetMapping("/books/author/page/{id}")
    @ResponseBody
    public BooksPageDto getNextPageAuthorBooks(@PathVariable(value = "id") Integer id,
                                               Integer offset, Integer limit){
        return new BooksPageDto(bookService.getBooksByAuthorId(id, offset, limit).getContent());
    }

    @GetMapping("/books/authors/{id}")
    public String getAuthorPageBooks(@PathVariable(value = "id") Integer id, Model model){
        model.addAttribute("author", authorService.getAuthorById(id));
        model.addAttribute("booksAuthor", bookService.getBooksByAuthorId(id, 0, 6).getContent());
        return "/books/author";
    }
}
