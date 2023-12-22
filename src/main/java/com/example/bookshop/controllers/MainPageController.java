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

    @ModelAttribute("tags")
    public List<TagEntity> tagsList(){
        return tagService.getTags();
    }

    @GetMapping("/")
    public String mainPage(HttpServletResponse response, HttpServletRequest request){
        cookieService.addUserCookie(response, request);
       return "index";
    }
    @GetMapping("/api/books/recommended/page")
    @ResponseBody
    public BooksPageDto getPage(@RequestParam("offset") Integer offset,
                                @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, limit).getContent());
    }
    @GetMapping("/api/recent/page")
    @ResponseBody
    public BooksPageDto getRecentBooks(@RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer limit){
        return new BooksPageDto(bookService.getPageRecentSlider(offset, limit).getContent());
    }
    @GetMapping("/api/popular")
    public String popularBookPage() {
        return "/books/popular";
    }

    @GetMapping(value = "/api/popular/page")
    @ResponseBody
    public BooksPageDto getPopularPage(@RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }
    @GetMapping("/api/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false)
                                              SearchWordDto searchWordDto){
        return new BooksPageDto(bookService.
                getPageOfSearchResultsBooks(searchWordDto.getExample(), offset, limit).getContent());
    }
    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResult(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) throws EmptySearchException {
            if(searchWordDto != null) {
                model.addAttribute("searchWordDto", searchWordDto);
                model.addAttribute("searchResults",
                        bookService.getPageOfSearchResultsBooks(searchWordDto.getExample(), 0, 20).getContent());
                return "/search/index";
            }else{
                throw new EmptySearchException("Поиск по null не возможен.");
            }
    }
}
