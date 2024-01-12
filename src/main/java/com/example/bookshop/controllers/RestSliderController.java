package com.example.bookshop.controllers;

import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestSliderController {

    private final BookService bookService;

    @Autowired
    public RestSliderController(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping("/api/books/recommended/page")
    public BooksPageDto getPage(@RequestParam("offset") Integer offset,
                                @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }

    @GetMapping("/api/recent/page")
    public BooksPageDto getRecentBooks(@RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageRecentSlider(offset, limit).getContent());
    }

    @GetMapping(value = "/api/popular/page")
    public BooksPageDto getPopularPage(@RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping("/api/books/recently/page")
    public BooksPageDto handlerRecently(@RequestParam("offset") Integer offset,
                                        @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getListViewedBooksUser(offset, limit).getContent());
    }
}
