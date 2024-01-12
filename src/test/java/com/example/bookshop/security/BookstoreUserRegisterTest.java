package com.example.bookshop.security;

import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.jwt.JWTUtil;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@DisplayName("Проверка класса BookstoreUserRegister")
class BookstoreUserRegisterTest {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepositoryMock;
    private final AuthenticationManager authenticationManager;
    private final UserContactRepository userContactRepositoryMock;


    @Autowired
    public BookstoreUserRegisterTest(BookstoreUserDetailsService bookstoreUserDetailsService,
                                     JWTUtil jwtUtil, UserRepository userRepositoryMock,
                                     AuthenticationManager authenticationManager, UserContactRepository userContactRepositoryMock) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userRepositoryMock = userRepositoryMock;
        this.userContactRepositoryMock = userContactRepositoryMock;
    }

    @Test
    @DisplayName("Проверка метода авторизации пользователя по номеру телефона")
    void jwtLoginByPhoneNumberTest() {
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setCode("25 12");
        payload.setContact("+7(911) 567-89-93");
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());

        UserEntity user = userRepositoryMock.findUserEntityByContact(payload.getContact());

        assertThat(userDetails.getUsername()).isEqualTo("+7(911) 567-89-93");
        assertThat(user.getName()).isEqualTo("adminShop");

    }

    @Test
    @DisplayName("Проверка метода авторизации пользователя по emal")
    void jwtLoginTest(){
        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setCode("123456789");
        payload.setContact("rabota822@bk.ru");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));

        UserContactEntity contact =
                userContactRepositoryMock.findFirstByContactAndType(payload.getContact(), ContactType.EMAIL);
        BookstoreUserDetails userDetails = (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        assertThat(userDetails.getName()).isEqualTo("adminShop");
        assertNotNull(contact);

    }
}