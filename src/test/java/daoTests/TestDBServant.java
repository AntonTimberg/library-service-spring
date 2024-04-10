package daoTests;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@Testcontainers
public class TestDBServant {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {

        postgresqlContainer.start();
        System.setProperty("env", "test");
        System.setProperty("test.jdbc.url", postgresqlContainer.getJdbcUrl());
        System.setProperty("test.jdbc.username", postgresqlContainer.getUsername());
        System.setProperty("test.jdbc.password", postgresqlContainer.getPassword());
    }

    public static void initializeTestDB() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE SCHEMA IF NOT EXISTS lib;");
            createTables(statement);
        }
    }

    public static void cleanUpTestDB() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE lib.book_genre, lib.books, lib.authors, lib.genres RESTART IDENTITY CASCADE;");
        }
    }

    public static void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword());
    }


    private static void createTables(Statement statement) throws SQLException {
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
                        "FOREIGN KEY (author_id) REFERENCES lib.authors(id) ON DELETE CASCADE" +
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

    public static void disableAutoCommit(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }
}
