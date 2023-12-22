package bookshop.selenium;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DisplayName("Комплексное автоматизированное тестирование сайта")

class MainPageSeleniumTests {
    private static ChromeDriver driver;
    private static final String CART = "CART";
    @BeforeAll
    static void setup(){
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\Desktop\\chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown(){
        driver.close();
    }
    @Test
    void testMainPageAccessTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void testMainPageSearchByQueryTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpSearchToken("Virunga")
                .pause()
                .submit()
                .pause();

        assertTrue(driver.getPageSource().contains("Virunga"));
    }
    @Test
    @DisplayName("Навигация по сайту")
    void nevigatelTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickPageGenres()
                .pause()
                .clickPageNews()
                .pause()
                .clickPagePopular()
                .pause()
                .clickPageAuthors()
                .clickAuthor()
                .pause()
                .clickPageMain()
                .pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя")
    void authUserBookShopTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickPageSignin()
                .pause()
                .clickLoginEmail()
                .pause()
                .inputEmailLogin("q@q.ru")
                .pause()
                .clickSedAuth()
                .pause()
                .inputPassLogin("123456")
                .pause()
                .clickLogin()
                .pause()
                .pause();
    }

    @Test
    @DisplayName("Проверка регистрации нового порльзователя")
    void regNewUserTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickPageSignin()
                .pause()
                .clickSigUp()
                .pause()
                .inputName("Василий")
                .pause()
                .inputPhone("9314161609")
                .pause()
                .clickFromInput()
                .pause()
                .clickFromInput()
                .pause()
                .clickSubmitPhone()
                .pause()
                .inputPhoneCode("555789")
                .pause()
                .inputEmail("tester@test.ru")
                .clickFromInput()
                .pause()
                .clickSubmitEmail()
                .pause()
                .inputEmailCode("898789")
                .pause()
                .inputPass("123456")
                .pause()
                .clickFromInput()
                .pause()
                .clickRegButton()
                .pause()
                .clickLoginEmail()
                .pause()
                .inputEmailLogin("tester@test.ru")
                .pause()
                .clickSedAuth()
                .pause()
                .inputPassLogin("123456")
                .pause()
                .clickLogin()
                .pause()
                .pause();;

        assertTrue(driver.getPageSource().contains("Василий"));
    }

    @Test
    @DisplayName("Проверка добавления книги в корзину авторизированным пользователем")
    void addBookCartAuthUserTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickPageSignin()
                .pause()
                .clickLoginEmail()
                .pause()
                .inputEmailLogin("q@q.ru")
                .pause()
                .clickSedAuth()
                .pause()
                .inputPassLogin("123456")
                .pause()
                .clickLogin()
                .pause()
                .clickLogoSite()
                .pause()
                .clickBookSlug()
                .pause()
                .clickPaidBook()
                .pause()
                .clickCartPage()
                .pause();
    }

    @Test
    @DisplayName("Проверка добавления книги в корзину анонимным пользователем")
    void addBookCartAnyUserTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickBookSlug()
                .pause()
                .clickPaidBook()
                .pause()
                .clickCartPage()
                .pause();

        Set<Cookie> cookies = driver.manage().getCookies();
        assertTrue(cookies.contains(CART + "="));
    }
}