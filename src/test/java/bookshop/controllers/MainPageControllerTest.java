package com.example.bookshop.controllers;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@DisplayName("Проверка контроллера главной страницы")
class MainPageControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public MainPageControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Проверка перехода на главную страницу")
    public void mainPageAccessTest() throws Exception{
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(content().string(containsString("")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка перехода на страницу signin не аворизированных пользователей")
    public void accessOnlyAuthorizedPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/signin"));
    }

    @Test
    @DisplayName("Проверка входа пользователя")
    public void correctLoginTest() throws Exception{
           mockMvc.perform(formLogin("/signin").user("q@q.ru").password("123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

    }

    @Test
    @WithUserDetails("q@q.ru")
    @DisplayName("Проверка аунтентификации пользователя")
    public void testAuthenticatedAccessToProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/span[1]")
                        .string("Дмитрий Петров"));
    }

    @Test
    @DisplayName("Проверка поиска")
    public void testSearchQuery() throws Exception {
        mockMvc.perform(get("/search/Virunga"))
                .andDo(print())
                .andExpect(xpath("/html/body/div/div/main/div[2]/div/div[1]/div[2]/strong/a")
                        .string("Virunga"));
    }

}