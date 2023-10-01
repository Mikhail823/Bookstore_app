package com.example.bookshop.controllers.user;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.service.BookService;
import com.example.bookshop.struct.book.BookEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class BookShopCartControllerTest {

    private final MockMvc mockMvc;
    private final BookRepository bookRepository;

    @MockBean
    private BookService bookServiceMock;
    private BookEntity book;
    @Autowired
    public BookShopCartControllerTest(MockMvc mockMvc, BookRepository bookRepository){
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
    }

    @BeforeEach
    void setUp() {
        this.book = bookRepository.findBookEntityBySlug("book-ita-031");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handleCartRequestTest() {
    }

    @Test
    void handleChangeBookStatusTest() throws Exception {
        Mockito.when(bookServiceMock.getBookPageSlug("book-ita-031")).thenReturn(book);
//      mockMvc.perform(post("/api/books//changeBookStatus/book-ita-031"))
//                .andDo(print()).andExpect(authenticated()).andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    void handleRemoveBookFromCartRequestTest() {
    }
}