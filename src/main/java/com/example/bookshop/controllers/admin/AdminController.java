package com.example.bookshop.controllers.admin;

import com.example.bookshop.dto.AuthorDto;
import com.example.bookshop.dto.BookDto;
import com.example.bookshop.service.*;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final static String REDIRECT = "redirect:/api/admin/users";
    private final static String REDIRECT_GENRES = "redirect:/api/books/genres";
    @Autowired
    private final AdminService adminService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final BookService bookService;
    @Autowired
    private final GenreService genreService;
    @Autowired
    private final TagService tagService;
    @Autowired
    private final BookReviewService bookReviewService;
    @Autowired
    private final AuthorService authorService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String handlerPageAdminUsers(Model model){
        model.addAttribute("users", adminService.getAllUsersShop());
        return "/admin/admin";
    }

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String handlerPageBooks(Model model){
        model.addAttribute("books", adminService.getAllBooks());
        return "/admin/booksShop";
    }

    @PostMapping("/deleteReview/{slug}/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUserReview(@PathVariable("slug") String slug, @PathVariable("id") Integer id){
        bookReviewService.deleteReviewUser(id);
        return "redirect:/api/books/" + slug;
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUserOfShop(@PathVariable("id") Integer id){
        UserEntity user = userService.getUserById(id);
        adminService.deleteUser(user);
        return REDIRECT;
    }

    @GetMapping("/addAuthor")
    public String handlerPageAddAuthor(Model model){
        model.addAttribute("authorDto", new AuthorDto());
        return "/admin/newAuthor";
    }

    @PostMapping("/save-author")
    public String saveNewAuthor(AuthorDto authorDto, Model model){
        authorService.saveNewAuthor(authorDto);
        model.addAttribute("saveAuthor", true);
        return "/admin/newAuthor";
    }

    @GetMapping("/addBook")
    public String handlerPageAddBook(Model model){

        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", genreService.getAllGenres());
        model.addAttribute("tags", tagService.getTags());
        return "/admin/addBooks";
    }

    @PostMapping("/save-book")
    public String saveNewBook(BookDto bookDto, Model model){
        bookService.addBook(bookDto);
        model.addAttribute("saveBook", true);
        return "/admin/addBooks";
    }

    @PostMapping("/delete-book/{book}")
    public String deleteBookShop(@PathVariable("book")BookEntity book){
        adminService.deleteBook(book);
        return REDIRECT_GENRES;
    }
}
