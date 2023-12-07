package com.example.bookshop.controllers;

import com.example.bookshop.dto.*;
import com.example.bookshop.security.ContactConfirmationPayload;
import com.example.bookshop.security.ContactConfirmationResponse;
import com.example.bookshop.security.exception.RequestException;
import com.example.bookshop.service.*;
import com.example.bookshop.service.response.TrueResponse;
import com.example.bookshop.struct.book.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

import static com.example.bookshop.service.util.DateFormatter.getToDateFormat;

@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BooksController {
    @Autowired
    private final BookService bookService;
    @Autowired
    private final BooksRatingAndPopulatityService booksRatingAndPopulatityService;
    @Autowired
    private final ResourceStorageService storage;
    @Autowired
    private final BookReviewService bookReviewService;
    @Autowired
    private final ViewedBooksService viewedBooksService;

    private final static String REDIRECT = "redirect:/api/books/";

    @PostMapping("/{slug}/img/save")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String saveNewBookImage(@RequestParam("file")MultipartFile file,
                                   @PathVariable("slug")String slug) throws IOException {

        String savePath = storage.saveNewBookImage(file,slug);
        BookEntity bookToUpdate = bookService.getBookPageSlug(slug);
        bookToUpdate.setImage(savePath);
        bookService.saveImageBook(bookToUpdate);

        return REDIRECT + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {

        Path path = storage.getBookFilePath(hash);
        MediaType mediaType = storage.getBookFileMime(hash);
        byte[] data = storage.getBookFileByteArray(hash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
    @GetMapping("/{slug}")
    public ModelAndView getSlugBookPage(@PathVariable(value = "slug") String slugBook,
                                        Model model, HttpServletRequest request){
        BookEntity book = bookService.getBookPageSlug(slugBook);
        viewedBooksService.saveViewedBooksUser(book, request);
        booksRatingAndPopulatityService.calculatingThePopularityOfBook(book);
        model.addAttribute("slugBook", bookService.getBookPageSlug(slugBook));
        model.addAttribute("totalRating", booksRatingAndPopulatityService.getTotalAndAvgStars(book.getId()));
        model.addAttribute("ratingBook", booksRatingAndPopulatityService.getRatingBook(book.getId()));
        model.addAttribute("reviewBook", bookService.getBookReview(book, 0, 4));
        return new ModelAndView("/books/slug");
    }

    @GetMapping("/popular")
    public String getPopularPage(Model model){
        model.addAttribute("booksPopular", bookService.getPageOfPopularBooks(0, 6).getContent());
        return "/books/popular";
    }

    @GetMapping("/popular/page")
    @ResponseBody
    public BooksPageDto getPagePopularBooks(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, limit).getContent());
    }

    @GetMapping("/recent")
    public String getRecent(Model model) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        model.addAttribute("newsBooks", bookService.getPageOfRecentBooksData(calendar.getTime(), new Date(), 0, 5));
        model.addAttribute("dateFrom", calendar.getTime());
        model.addAttribute("dateTo", new Date());
        return "/books/recent";
    }

    @GetMapping("/recent/page")
    @ResponseBody
    public BooksPageDto getNextSearchPage(
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit,
            @RequestParam(value = "from", defaultValue = "") String from,
            @RequestParam(value = "to", defaultValue = "") String to) {
        return new BooksPageDto(bookService.getPageOfRecentBooksData(getToDateFormat(from),
                getToDateFormat(to), offset, limit).getContent());
    }

    @PostMapping("/rateBook/{slug}")
    public String saveRatingBook(@RequestBody RatingRequestDto requestDto,
                                 @PathVariable(name = "slug") String slug) throws RequestException {

            booksRatingAndPopulatityService.ratingBookSave(requestDto.getValue(),
                    booksRatingAndPopulatityService.getRatingBook(bookService.getBookPageSlug(slug).getId()));

        return REDIRECT + slug;
    }

    @PostMapping(value = "/bookReview/{slug}")
    public ModelAndView addBookReview(@PathVariable("slug") String slug,
                                      @PathParam("text") String text){
        bookReviewService.saveReviewText(slug, text);
        return new ModelAndView(REDIRECT + slug);
    }



    @PostMapping("/rateBookReview/{slug}")
    public String likeTheReviewBook(@RequestBody LikeReviewBookDto reviewBookDto,
                                    @PathVariable("slug") String slug) throws RequestException {
        booksRatingAndPopulatityService.saveLikeReviewBook(reviewBookDto);
        booksRatingAndPopulatityService.saveRatingReview(reviewBookDto.getReviewid());
        return REDIRECT + slug;
    }

    @GetMapping("/recently")
    public String handlerPageRecently(Model model, HttpServletRequest request){
        BooksPageDto booksPageDto = new BooksPageDto(bookService.getListViewedBooksUser(0, 6, request).getContent());
        model.addAttribute("booksRecently", booksPageDto.getBooks());
        return "/books/recently_viewed";
    }

    @GetMapping("/recently/page")
    @ResponseBody
    public BooksPageDto handlerRecently(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit, HttpServletRequest request){
        return new BooksPageDto(bookService.getListViewedBooksUser(offset, limit, request).getContent());
    }
}
