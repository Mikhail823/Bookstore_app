package com.example.bookshop.controllers;

import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.dto.SearchWordDto;
import com.example.bookshop.exeption.EmptySearchException;


import com.example.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController {

    private final BookService bookService;

    @Autowired
    public SearchController(BookService bookService){
        this.bookService = bookService;
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
