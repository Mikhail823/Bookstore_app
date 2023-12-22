package bookshop.repository;

import com.example.bookshop.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@DisplayName("Тестирование UserRepository")
class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository){
        this.userRepository = userRepository;
    }

//    @Test
//    @DisplayName("Добавление нового пользователя в базу данных")
//    public void testAddNewUser(){
//
//        UserEntity user = new UserEntity();
//        user.setName("Nikolai");
//        user.setEmail("tests@test.test");
//        user.setPhone("+7(999)999-99-99");
//        user.setPassword("123456");
//        user.setHash("jdnfjhvfbhb56");
//        user.setRegTime(new Date());
//        user.setRoleType(RoleType.USER);
//        user.setBalance(0);
//
//        assertNotNull(userRepository.save(user));
//    }

}