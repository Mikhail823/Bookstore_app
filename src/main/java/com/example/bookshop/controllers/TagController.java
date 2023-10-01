package com.example.bookshop.controllers;

import com.example.bookshop.dto.BooksPageDto;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.TagService;
import com.example.bookshop.struct.book.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    @Autowired
    private final TagService tagService;
    @Autowired
    private final BookService bookService;

    @GetMapping("/tags/{id}")
    public String getTagPage(@PathVariable(value = "id") Integer id, Model model){
       Page<BookEntity> books = bookService.getBooksOfTags(id, 0, 5);
        model.addAttribute("booksTag", books.getContent());
        model.addAttribute("tag", tagService.gatOneTag(id));
        return "/tags/index";
   }

   @GetMapping("/books/tag/{id}")
   @ResponseBody
    public BooksPageDto getNextPage(@PathVariable(value = "id") Integer id, Integer offset, Integer limit){
        return new BooksPageDto(bookService.getBooksOfTags(id, offset, limit).getContent());
   }
}
