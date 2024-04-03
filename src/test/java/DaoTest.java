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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@Testcontainers
public class DaoTest {
    @Container
    public static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("env", "test");
        System.setProperty("test.jdbc.url", postgresqlContainer.getJdbcUrl());
        System.setProperty("test.jdbc.username", postgresqlContainer.getUsername());
        System.setProperty("test.jdbc.password", postgresqlContainer.getPassword());

        connection = DriverManager.getConnection(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword());

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS lib;");
            createTables(statement);
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        try (Connection connection = DriverManager.getConnection(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE lib.book_genre, lib.books, lib.authors, lib.genres RESTART IDENTITY CASCADE;");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при очистке таблиц после теста", e);
        } finally {
            System.clearProperty("env");
            System.clearProperty("test.jdbc.url");
            System.clearProperty("test.jdbc.username");
            System.clearProperty("test.jdbc.password");
        }
    }

    private void createTables(Statement statement) throws SQLException {
        statement.execute(
                "CREATE TABLE IF NOT EXISTS lib.authors (" +
                        "id SERIAL PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL" +
                        ");"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS lib.genres (" +
                        "id SERIAL PRIMARY KEY," +
                        "name VARCHAR(255) NOT NULL" +
                        ");"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS lib.books (\n" +
                        "    id SERIAL PRIMARY KEY,\n" +
                        "    title VARCHAR(255) NOT NULL,\n" +
                        "    author_id INTEGER NOT NULL,\n" +
                        "    FOREIGN KEY (author_id) REFERENCES lib.authors(id) ON DELETE CASCADE\n" +
                        ");"
        );

        statement.execute(
                "CREATE TABLE IF NOT EXISTS lib.book_genre (" +
                        "book_id INTEGER NOT NULL," +
                        "genre_id INTEGER NOT NULL," +
                        "PRIMARY KEY (book_id, genre_id)," +
                        "FOREIGN KEY (book_id) REFERENCES lib.books(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (genre_id) REFERENCES lib.genres(id) ON DELETE CASCADE" +
                        ");"
        );
    }

    @Test
    void addingSevenAuthorsAndDeleteTwo() {
        AuthorDAO authorDAO = new AuthorDAOImpl();

        authorDAO.save(new Author("Первый"));
        authorDAO.save(new Author("Второй"));
        authorDAO.save(new Author("Третий"));
        authorDAO.save(new Author("Четвертый"));
        authorDAO.save(new Author("Пятый"));
        authorDAO.save(new Author("Шестой"));
        authorDAO.save(new Author("Седьмой"));

        List<Author> authors = authorDAO.findAll();

        authorDAO.delete(authors.get(4).getId());
        authorDAO.delete(authors.get(5).getId());

        assertEquals(5, authorDAO.findAll().size());
    }

    @Test
    void authorAddingAndFind() {
        AuthorDAO authorDAO = new AuthorDAOImpl();

        authorDAO.save(new Author("Первый"));

        assertEquals("Первый", authorDAO.findById(1).get().getName());
    }

    @Test
    void authroAddingAndUpdate() {
        AuthorDAO authorDAO = new AuthorDAOImpl();

        authorDAO.save(new Author("Первый"));
        Author author = authorDAO.findAll().get(0);
        author.setName("Последний");
        authorDAO.update(author);

        assertEquals("Последний", authorDAO.findById(1).get().getName());
    }

    @Test
    void addingSevenGenresAndDeleteTwo() {
        GenreDAO genreDAO = new GenreDAOImpl();

        genreDAO.save(new Genre("Первый"));
        genreDAO.save(new Genre("Второй"));
        genreDAO.save(new Genre("Третий"));
        genreDAO.save(new Genre("Четвертый"));
        genreDAO.save(new Genre("Пятый"));
        genreDAO.save(new Genre("Шестой"));
        genreDAO.save(new Genre("Седьмой"));

        List<Genre> authors = genreDAO.findAll();

        genreDAO.delete(authors.get(4).getId());
        genreDAO.delete(authors.get(5).getId());

        assertEquals(5, genreDAO.findAll().size());
    }

    @Test
    void genreSaveAndFindById() {
        GenreDAO genreDAO = new GenreDAOImpl();
        Genre genre = new Genre();
        genre.setName("Криминал");
        genreDAO.save(genre);

        List<Genre> allGenres = genreDAO.findAll();

        Genre savedGenre = allGenres.get(0);
        Optional<Genre> foundGenre = genreDAO.findById(savedGenre.getId());
        assertEquals("Криминал", foundGenre.get().getName());
    }

    @Test
    void genreUpdateTest() {
        GenreDAO genreDAO = new GenreDAOImpl();
        Genre genre = new Genre();
        genre.setName("Sci-Fi");
        genreDAO.save(genre);

        Genre savedGenre = genreDAO.findAll().get(0);
        savedGenre.setName("Не сай фай");
        genreDAO.update(savedGenre);

        Optional<Genre> updatedGenre = genreDAO.findById(savedGenre.getId());
        assertEquals("Не сай фай", updatedGenre.get().getName());
    }

    @Test
    void addBookAndTestRelationWithAuthor() {
        AuthorDAO authorDAO = new AuthorDAOImpl();
        BookDAO bookDAO = new BookDAOImpl();
        GenreDAO genreDAO = new GenreDAOImpl();

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
            fail("Должен быть хотя бы один автор и жанр");
        }
    }

    @Test
    void addBookThenDeleteAuthorAndCheckBookExistence() {
        AuthorDAO authorDAO = new AuthorDAOImpl();
        BookDAO bookDAO = new BookDAOImpl();
        GenreDAO genreDAO = new GenreDAOImpl();

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
}
