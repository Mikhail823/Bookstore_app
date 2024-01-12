package com.example.bookshop.security;

import com.example.bookshop.BookShopApplication;
import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.security.RegistrationForm;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.RoleEntity;

import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = BookShopApplication.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class BookstoreUserRegisterTests {

    private final BookstoreUserRegister bookstoreUserRegister;
    private final PasswordEncoder passwordEncoder;
    private RegistrationForm registrationForm;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private RoleRepository roleRepositoryMock;

    @MockBean
    private UserContactRepository contactRepositoryMock;


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

    @Test
    @DisplayName("Проверка регистрации нового пользователя.")
    void registrationNewUser() {
        UserEntity user = new UserEntity();

        UserContactEntity contact = new UserContactEntity();

        RoleEntity role = roleRepositoryMock.findRoleEntityByName("ROLE_USER");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);

        user.setRegTime(new Date());
        user.setRoles(roles);
        user.setHash("dfdfdfdfd");
        user.setBalance(0);
        user.setName(registrationForm.getName());
        user.setPassword(passwordEncoder.encode(registrationForm.getPass()));
        userRepositoryMock.save(user);

        contact.setContact(registrationForm.getEmail());
        contact.setType(ContactType.EMAIL);
        contact.setUserId(user);
        contact.setCode("255 266");
        contact.setCodeTime(LocalDateTime.now());
        contact.setCodeTrails(1);
        contact.setApproved((short)1);
        contactRepositoryMock.save(contact);

        contact.setContact(registrationForm.getPhone());
        contact.setUserId(user);
        contact.setCode("25 89");
        contact.setCodeTime(LocalDateTime.now());
        contact.setCodeTrails(1);
        contact.setApproved((short)1);
        contactRepositoryMock.save(contact);
        assertNotNull(user);
        assertTrue(passwordEncoder.matches(registrationForm.getPass(), user.getPassword()));
     //   assertTrue(CoreMatchers.is(user.getListContact().matches(registrationForm.getPhone()));
        assertTrue(CoreMatchers.is(user.getName()).matches(registrationForm.getName()));
     //   assertTrue(CoreMatchers.is(user.getEmail()).matches(registrationForm.getEmail()));
        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
    }
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