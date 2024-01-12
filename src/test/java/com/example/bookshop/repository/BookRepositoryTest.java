package com.example.bookshop.repository;

import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.struct.book.BookEntity;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestPropertySource("/application-test.properties")
@DisplayName("Тестирование BookRepository")
@RequiredArgsConstructor
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Поиск книг по названию")
    void findBooksByTitleContaining() {

        String token = "Vir";
        List<BookEntity> listBookByTitle = bookRepository.findBooksByTitleContaining(token);

        assertNotNull(listBookByTitle);

        assertFalse(listBookByTitle.isEmpty());

        for (BookEntity book : listBookByTitle){

            assertThat(book.getTitle().contains(token));

        }
    }

//    @Test
//    @DisplayName("Поиск книг по имени автора")
//    void findBookEntityByAuthor_FirstName() {
//
//        String token = "Daryl";
//        List<BookEntity> listBookByAuthorFirstName = bookRepository.findBookEntityByAuthor_FirstName(token);
//
//        assertNotNull(listBookByAuthorFirstName);
//
//        assertFalse(listBookByAuthorFirstName.isEmpty());
//
//        for(BookEntity book : listBookByAuthorFirstName){
//
//            assertThat(book.getAuthor().getFirstName().contains(token));
//
//        }
//    }

    @Test
    @DisplayName("Нахождение бестеллеров")
    void getBestsellers() {

        List<BookEntity> bestSellerListBook = bookRepository.getBestsellers();

        assertNotNull(bestSellerListBook);

        assertFalse(bestSellerListBook.isEmpty());

        assertThat(bestSellerListBook.size()).isGreaterThan(1);
    }
}