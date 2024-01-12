package com.example.bookshop.controllers.user;

import com.example.bookshop.service.BookService;
import com.example.bookshop.service.components.CookieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookShopCartControllerTest {


    private final MockMvc mockMvc;
    @MockBean
    private final CookieService cookieService;
    @MockBean
    private final BookService bookService;
    @Autowired
    public BookShopCartControllerTest(MockMvc mockMvc, CookieService cookieService, BookService bookService){
        this.mockMvc = mockMvc;
        this.cookieService = cookieService;
        this.bookService = bookService;
    }
    @Test
    @DisplayName("Проверка перехода на страницу cart")
    void handleCartRequest() {
        try {

            mockMvc.perform(get("/"));
            mockMvc.perform(get("/api/books/cart"))
                    .andDo(print())
                    .andExpect(status().isOk());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void bookCart() {

    }

    @Test
    void handleChangeBookStatus() {


    }

    @Test
    void handleRemoveBookFromCartRequest() {
    }

    @Test
    void handlerPay() {
    }
}