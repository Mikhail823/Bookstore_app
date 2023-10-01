package com.example.bookshop;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookShopApplicationTests {

    @Value("${auth.secret}")
    private String authSecret;
    private final BookShopApplication application;

    @Autowired
    public BookShopApplicationTests(BookShopApplication application){
        this.application = application;
    }

    @Test
    void contextLoads() {
        assertNotNull(application);
    }

    @Test
    void verifyAuthSecret(){
        assertThat(authSecret, Matchers.containsString("box"));
    }

}
