package com.example.bookshop.controllers.user;

import com.example.bookshop.security.BookstoreUserDetails;

import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.links.Book2UserTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/books")
public class BookShopPaidController {

    private static final String REDIRECT_SLUG = "redirect:/api/books/";
    private static final String REDIRECT_PROFILE = "http://localhost:8081/profile";

    private final BookService bookService;

    @Autowired
    public BookShopPaidController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/changeBookStatus/archived/{slug}")
    public String handleChangeBookArhived(@PathVariable(name = "slug") String slug,
                                          @AuthenticationPrincipal BookstoreUserDetails user){

        BookEntity book = bookService.getBookPageSlug(slug);
        bookService.saveBookUser(book, user.getContact().getUserId(), Book2UserTypeEntity.StatusBookType.ARCHIVED);

        return REDIRECT_SLUG + slug;
    }
}
