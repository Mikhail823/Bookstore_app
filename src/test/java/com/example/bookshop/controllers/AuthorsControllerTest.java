package com.example.bookshop.controllers;

import com.example.bookshop.service.AuthorService;
import lombok.SneakyThrows;
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
class AuthorsControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private AuthorService authorServiceMock;

    @Autowired
    public AuthorsControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @SneakyThrows
    @Test
    void authorsPage() {
        mockMvc.perform(get("/api/authors"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getPageAuthor() {
        mockMvc.perform(get("/api/author/5"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void getNextPageAuthorBooks() {
    }

    @Test
    void getAuthorPageBooks() {
    }
}