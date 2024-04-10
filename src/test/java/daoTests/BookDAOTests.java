package daoTests;

import org.example.dao.AuthorDAO;
import org.example.dao.AuthorDAOImpl;
import org.example.dao.BookDAO;
import org.example.dao.BookDAOImpl;
import org.example.dao.GenreDAO;
import org.example.dao.GenreDAOImpl;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookDAOTests {
    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    private Connection connection;
    private AuthorDAO authorDAO;

    @BeforeEach
    void setUp() throws Exception {
        TestDBServant.initializeTestDB();
        authorDAO = new AuthorDAOImpl();
        genreDAO = new GenreDAOImpl();
        bookDAO = new BookDAOImpl();
        connection = TestDBServant.getConnection();
        TestDBServant.disableAutoCommit(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        TestDBServant.rollbackTransaction(connection);
        TestDBServant.cleanUpTestDB();
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void addBookAndTestRelationWithAuthor() {
        authorDAO.save(new Author("Первый"));
        genreDAO.save(new Genre("Научная литература"));

        List<Author> authors = authorDAO.findAll();
        List<Genre> genres = genreDAO.findAll();

        if (!authors.isEmpty() && !genres.isEmpty()) {
            Book book = new Book();
            book.setAuthorId(authors.get(0).getId());
            book.setTitle("Книга #1");
            book.setGenres(genres);

            bookDAO.save(book);

            Author author = authorDAO.findAll().get(0);
            assertEquals(author.getBooks().get(0).getId(), bookDAO.findAll().get(0).getId());
        } else {
            fail("Должен быть хотя бы один автор");
        }
    }

    @Test
    void addBookThenDeleteAuthorAndCheckBookExistence() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));

        Book book = new Book();
        book.setTitle("Книга");
        Author author = authorDAO.findById(1).get();
        book.setAuthorId(author.getId());
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDAO.findById(1).get());
        book.setGenres(genres);
        Book savedBook = bookDAO.save(book);

        authorDAO.delete(author.getId());

        Book foundBook = bookDAO.findById(savedBook.getId()).orElse(null);
        assertNull("Книга должна быть удалена вместе с автором", foundBook);
    }

    @Test
    void findById() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));

        Book book = new Book();
        book.setTitle("Книга");
        Author author = authorDAO.findById(1).get();
        book.setAuthorId(author.getId());
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDAO.findById(1).get());
        book.setGenres(genres);
        Book savedBook = bookDAO.save(book);

        Optional<Book> foundBook = bookDAO.findById(savedBook.getId());
        assertTrue(foundBook.isPresent(), "Книга не найдена по ID.");
        assertEquals("Книга", foundBook.get().getTitle());
    }

    @Test
    void findAllBooks() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));

        Author author = authorDAO.findById(1).get();
        bookDAO.save(new Book("Book 1", author.getId()));
        bookDAO.save(new Book("Book 2", author.getId()));

        List<Book> books = bookDAO.findAll();
        assertTrue(books.size() == 2, "Не найдено достаточное количество книг.");
    }

    @Test
    void saveBookAndCheckGenres() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));
        genreDAO.save(new Genre("Жанр 2"));

        Book book = new Book();
        book.setTitle("Книга");
        Author author = authorDAO.findById(1).get();
        book.setAuthorId(author.getId());
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDAO.findById(1).get());
        genres.add(genreDAO.findById(2).get());
        book.setGenres(genres);
        bookDAO.save(book);

        Optional<Book> foundBook = bookDAO.findById(book.getId());
        assertTrue(foundBook.isPresent(), "Книга с жанрами не сохранена.");
        assertEquals(2, foundBook.get().getGenres().size());
    }

    @Test
    void testUpdateBook() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));

        Book book = new Book();
        book.setTitle("Книга");
        Author author = authorDAO.findById(1).get();
        book.setAuthorId(author.getId());
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDAO.findById(1).get());
        book.setGenres(genres);
        Book savedBook = bookDAO.save(book);

        savedBook.setTitle("Новое имя");
        bookDAO.update(book);

        Optional<Book> updatedBook = bookDAO.findById(book.getId());
        assertTrue(updatedBook.isPresent(), "Книга для обновления не найдена.");
        assertEquals("Новое имя", updatedBook.get().getTitle());
    }

    @Test
    void testDeleteBook() {
        authorDAO.save(new Author("Автор"));
        genreDAO.save(new Genre("Жанр"));

        Book book = new Book();
        book.setTitle("Книга");
        Author author = authorDAO.findById(1).get();
        book.setAuthorId(author.getId());
        List<Genre> genres = new ArrayList<>();
        genres.add(genreDAO.findById(1).get());
        book.setGenres(genres);
        Book savedBook = bookDAO.save(book);

        bookDAO.delete(savedBook.getId());

        Optional<Book> deletedBook = bookDAO.findById(book.getId());
        assertFalse(deletedBook.isPresent(), "Книга не была удалена.");
    }
}
