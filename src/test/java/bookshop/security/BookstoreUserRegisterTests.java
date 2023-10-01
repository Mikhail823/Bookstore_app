package com.example.bookshop.security;

import com.example.bookshop.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest

class BookstoreUserRegisterTests {

    private final BookstoreUserRegister bookstoreUserRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository userRepositoryMock;

    @Autowired
    public BookstoreUserRegisterTests(BookstoreUserRegister bookstoreUserRegister,
                                      PasswordEncoder passwordEncoder) {
        this.bookstoreUserRegister = bookstoreUserRegister;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setName("Mikhail");
        registrationForm.setEmail("test@test.ru");
        registrationForm.setPass("123456");
        registrationForm.setPhone("+7(931)416-16-09");

    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

//    @Test
//    @DisplayName("Проверка регистрации нового пользователя.")
//    void registrationNewUser() {
//        UserEntity user = new UserEntity();
//        UserContactEntity contact = new UserContactEntity();
//        user.setRegTime(new Date());
//        user.setRoleType(RoleType.USER);
//        user.setHash("dfdfdfdfd");
//        user.setBalance(0);
//        user.setName(registrationForm.getName());
//        contact
//        contact.setContact(registrationForm.getEmail());
//        contact.setType(ContactType.EMAIL);
//        user.setPhone(registrationForm.getPhone());
//        user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
//        userRepositoryMock.save(user);
//        assertNotNull(user);
//        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
//        assertTrue(CoreMatchers.is(user.getPhone()).matches(registrationForm.getPhone()));
//        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
//        assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));
//        Mockito.verify(userRepositoryMock, Mockito.times(1))
//                .save(Mockito.any(UserEntity.class));
//    }
//
//    @Test
//    void rigistrationNewUserFail(){
//        Mockito.doReturn(new UserEntity())
//                .when(userRepositoryMock)
//                .findUserEntityByEmail(registrationForm.getEmail());
//
//        UserEntity user = bookstoreUserRegister.registrationNewUser(registrationForm);
//        assertNull(user);
//    }

}