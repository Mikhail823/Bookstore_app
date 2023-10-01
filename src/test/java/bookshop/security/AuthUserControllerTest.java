package com.example.bookshop.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@DisplayName("Проверка контроллера пользователя")
class AuthUserControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private BookstoreUserRegister userRegister;

    @Autowired
    public AuthUserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @DisplayName("Проверка перехода на страницу входа")
    void handleSigninTest() throws Exception {

        mockMvc.perform(get("/signin"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка перехода на страницу регистрации")
    void handleSignUpTest() throws Exception {

        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk());
    }

//    @Test
//    void handleUserRegistrationTest() throws Exception {
//        mockMvc.perform(post("/reg")
//                        .content(asJsonString(new RegistrationForm()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful())
//                .andReturn();
//
//        Mockito.verify(userRegister, Mockito.times(1)).registrationNewUser(any());
//
//    }

    @Test
    @DisplayName("Проверка входа пользователя по email")
    void handleLoginTest() throws Exception {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("");
        doReturn(response)
                .when(userRegister)
                .jwtLogin(any());

        MvcResult mvcResult = mockMvc.perform(post("/login")
                        .content(asJsonString(new ContactConfirmationPayload()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Mockito.verify(userRegister, Mockito.times(1)).jwtLogin(any());

        assertEquals(response.getResult(), Objects.requireNonNull(mvcResult.getResponse().getCookie("token")).getValue());
    }

    @Test
    @DisplayName("Проверка (Logout) пользователя")
    void handleLogout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"))
                .andReturn();

        Cookie cookie = mvcResult.getResponse().getCookie("token");

        if (Objects.nonNull(cookie)) {

            assertNull(cookie.getValue());

        }
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}