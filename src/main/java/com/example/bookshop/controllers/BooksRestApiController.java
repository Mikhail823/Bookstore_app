package com.example.bookshop.controllers;

import com.example.bookshop.exeption.ApiResponse;
import com.example.bookshop.exeption.BookStoreApiWrongParameterException;
import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(description = "book data api")
public class BooksRestApiController {

    private final BookService bookService;


    @Autowired
    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books/by-title")
    @ApiOperation("get books by title")
    public ResponseEntity<ApiResponse<BookEntity>> booksByTitle(@RequestParam("title")String title) throws BookStoreApiWrongParameterException {
        ApiResponse<BookEntity> apiResponse = new ApiResponse<>();
        List<BookEntity> data = bookService.getBooksByTitle(title);
        apiResponse.setDebugMessage("successful request");
        apiResponse.setMessage("data size: " + data.size());
        apiResponse.setHttpStatus(HttpStatus.OK);
        apiResponse.setTimeStamp(LocalDateTime.now());
        apiResponse.setData(data);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/books/by-price-range")
    @ApiOperation("get books by price range from min price to max price")
    public ResponseEntity<List<BookEntity>> priceRangeBookss(@RequestParam("min")Integer min, @RequestParam("max")Integer max){
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max));
    }

    @GetMapping("/books/with-max-discount")
    @ApiOperation("get list of book with max price")
    public ResponseEntity<List<BookEntity>> maxPriceBooks(){
        return ResponseEntity.ok(bookService.getBooksWithMaxPrice());
    }

    @GetMapping("/books/bestsellers")
    @ApiOperation("get bestseller book (which is_bestseller = 1)")
    public ResponseEntity<List<BookEntity>> bestSellerBooks(){
        return ResponseEntity.ok(bookService.getBestsellers());
    }

    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<ApiResponse<BookEntity>> handler(Exception exception){
        return new ResponseEntity<>( new ApiResponse<BookEntity>(HttpStatus.BAD_REQUEST, "SatckOverflow",
                exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookStoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<BookEntity>> bookStoreHandler(Exception exception){
        return new ResponseEntity<>(new ApiResponse<BookEntity>(HttpStatus.BAD_REQUEST, "STACKOVERFLOW!!!!", exception),
                HttpStatus.BAD_REQUEST);
    }


}
