package com.example.bookshop.service.impl;

import com.example.bookshop.dto.BookDto;

import com.example.bookshop.repository.AuthorRepository;
import com.example.bookshop.repository.BookRepository;

import com.example.bookshop.repository.GenreRepository;
import com.example.bookshop.repository.TagRepository;
import com.example.bookshop.service.BookService;
import com.example.bookshop.service.util.DateFormatter;
import com.example.bookshop.struct.book.BookEntity;
import com.example.bookshop.struct.book.author.AuthorEntity;
import com.example.bookshop.struct.genre.GenreEntity;
import com.example.bookshop.struct.tags.TagEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@DisplayName("Проверка класса BookServiceImpl")
class BookServiceImplTest {

    private BookEntity book;
    private GenreEntity genre;
    private AuthorEntity author;
    private TagEntity tag;

    public static DateFormatter dateFormatter;

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    @MockBean
    private BookService bookServiceMock;

    @Autowired
    public BookServiceImplTest(BookRepository bookRepository, GenreRepository genreRepository,
                               AuthorRepository authorRepository, TagRepository tagRepository){
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @BeforeEach
    void setUp() {
        book = bookRepository.findBookEntityById(3);
        genre = genreRepository.getOne(1);
        author = authorRepository.getOne(2);
        tag = tagRepository.getOne(4);
    }

    @AfterEach
    void tearDown() {
        book = null;
        genre = null;
        author = null;
        tag = null;
    }

    @Test
    @DisplayName("Проверка получения списка книг по названию")
    void getBooksByTitle() {


        List<BookEntity> books = bookRepository.findBooksByTitleContaining("Tax");

        assertThat(books).asList().isNotNull();

    }

    @Test
    void getBooksWithPriceBetween() {
    }

    @Test
    void getBooksWithMaxPrice() {
    }

    @Test
    @DisplayName("Проверка получения списка бесцеллеров")
    void getBestsellers() {
        List<BookEntity> books = bookRepository.getBestsellers();

        for (BookEntity book : books){
            assertThat(book.getIsBesteller()).isEqualTo(1);
        }
    }

    @Test
    void getPageOfRecommendedBooks() {
    }

    @Test
    void getPageOfSearchResultsBooks() {
    }

    @Test
    void getPageOfRecentBooksData() {
    }

    @Test
    void getPageOfPopularBooks() {
    }

    @Test
    void getPageRecentSlider() {
    }

    @Test
    void getBooksOfGenre() {
    }

    @Test
    void getBooksByAuthorId() {
        Pageable next = PageRequest.of(0, 5);
        Page<BookEntity> bookList = bookRepository.findBookEntityByAuthorId(author.getId(), next);

        assertThat(bookList).isNotNull();

    }

    @Test
    void getBooksOfGenreType() {
    }

    @Test
    void saveImageBook() {
    }


    @Test
    void calculationCostOfBooksTheCartUser() {
    }

    @Test
    void saveBook2User() {
    }


    @Test
    void removeBook2User() {
    }

    @Test
    @DisplayName("Проверка обновления количества книги в корзине")
    void updateCountBooksCart() {

        bookRepository.updateCountCartBooks(book.getSlug(), 4);

        assertThat(book.getQuantityTheBasket()).isEqualTo(4);
    }

    @Test
    @DisplayName("Проверка добавления новой книги")
    void addBook() {
        BookDto bookDto = createBook();
        BookEntity newBook = new BookEntity();


        newBook.setIsBesteller(Integer.valueOf(bookDto.getBestseller()));
        newBook.setSlug("yrbg-879");
        newBook.setTagList(bookDto.getTags());
        newBook.setPriceOld(Integer.valueOf(bookDto.getDiscountPrice()));
        newBook.setPrice(Double.parseDouble(bookDto.getPrice()));
        newBook.setGenre(bookDto.getGenre());
        newBook.setAuthors(bookDto.getAuthors());
        newBook.setTitle(bookDto.getTitle());
        newBook.setDescription(bookDto.getDescription());
        newBook.setPubDate(dateFormatter.getToDateFormat(bookDto.getPubDate()));
        newBook.setImage(bookDto.getImage());
        bookRepository.save(newBook);
        List<BookEntity> bookList = bookRepository.findBooksByTitleContaining(bookDto.getTitle());


        assertThat(bookList).isNull();
    }


    @Test
    @DisplayName("Проверка обновления количества купленной книги")
    void updateCountPaidBooks() {

        bookRepository.updateCountPaidBooks(book.getSlug(), 2);

        assertThat(book.getNumberOfPurchased()).isEqualTo(2);
    }

    @Test
    @DisplayName("Проверка получения книги по id")
    void findBookById() {

        BookEntity book = bookRepository.findBookEntityById(5);

        assertThat(book.getId()).isEqualTo(5);
    }

    public BookDto createBook(){
        BookDto newBook = new BookDto();
        List<AuthorEntity> authorList = new ArrayList<>();
        List<TagEntity> tagList = new ArrayList<>();
        authorList.add(author);
        tagList.add(tag);
        newBook.setTitle("War and Word");
        newBook.setDescription("Быстро в полутьме разобрали лошадей, подтянули подпруги и разобрались по командам.");
        newBook.setImage("http://dummyimage.com/270x313.png/cc0000/ffffff");
        newBook.setGenre(genre);
        newBook.setPubDate("2021-11-04");
        newBook.setBestseller("1");
        newBook.setPrice("1500");
        newBook.setDiscountPrice("0");
        newBook.setAuthors(authorList);
        newBook.setTags(tagList);

        return newBook;
    }
}