package com.example.bookshop.controllers;

import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.dto.SearchWordDto;
import com.example.bookshop.exeption.EmptySearchException;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.TagService;
import com.example.bookshop.service.components.CookieService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.tags.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    private final CookieService cookieService;

    @Autowired
    public MainPageController(BookService bookService, TagService tagService, CookieService cookieService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.cookieService = cookieService;
    }

    @ModelAttribute("recommendedBooks")
    public List<BookEntity> recommendedBooks(){
        return bookService.getPageOfRecommendedBooks(0, 6).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<BookEntity> recentBooks(){
        return bookService.getPageRecentSlider(0, 6).getContent();
    }

    @ModelAttribute("popularBooks")
    public List<BookEntity> popularBooks(){
        return bookService.getPageOfPopularBooks(0, 6).getContent();
    }

    @ModelAttribute("booksRecently")
    public List<BookEntity> recentlyBooks() { return  bookService.getListViewedBooksUser(0, 6).getContent();}

    @ModelAttribute("tags")
    public List<TagEntity> tagsList(){
        return tagService.getTags();
    }

    @GetMapping("/")
    public String mainPage(HttpServletResponse response, HttpServletRequest request){
        cookieService.addUserCookie(response, request);
       return "index";
    }

    @GetMapping("/api/popular")
    public String popularBookPage() {
        return "/books/popular";
    }

    @GetMapping("/api/books/recently")
    public String handlerPageRecently(Model model, HttpServletRequest request){
        BooksPageDto booksPageDto = new BooksPageDto(bookService.getListViewedBooksUser(0, 6).getContent());
        model.addAttribute("booksRecently", booksPageDto.getBooks());
        return "/books/recently_viewed";
    }
}
