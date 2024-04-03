import org.example.dao.AuthorDAO;
import org.example.dao.AuthorDAOImpl;
import org.example.model.Author;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

@Testcontainers
public class AuthorDaoTest {

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
                "CREATE TABLE IF NOT EXISTS lib.books (" +
                        "id SERIAL PRIMARY KEY," +
                        "title VARCHAR(255) NOT NULL," +
                        "author_id INTEGER NOT NULL," +
                        "FOREIGN KEY (author_id) REFERENCES lib.authors(id)" +
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
    void addingAndFindAuthor() {
        AuthorDAO authorDAO = new AuthorDAOImpl();

        authorDAO.save(new Author("Первый"));

        assertEquals("Первый", authorDAO.findById(1).get().getName());
    }

    @Test
    void addingAndUpdateAuthor() {
        AuthorDAO authorDAO = new AuthorDAOImpl();

        authorDAO.save(new Author("Первый"));
        Author author = authorDAO.findAll().get(0);
        author.setName("Последний");
        authorDAO.update(author);

        assertEquals("Последний", authorDAO.findById(1).get().getName());
    }


}
