package org.example.dao;

import org.example.DBConnection;
import org.example.model.Book;
import org.example.model.Genre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {
    @Override
    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM lib.books WHERE id = ?";
        Book book = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthorId(resultSet.getInt("author_id"));
                book.setGenres(getGenresForBook(id, connection));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM lib.books";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthorId(resultSet.getInt("author_id"));
                book.setGenres(getGenresForBook(book.getId(), connection));
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public List<Book> findByAuthorId(int authorId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM lib.books WHERE author_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, authorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthorId(resultSet.getInt("author_id"));
                book.setGenres(getGenresForBook(book.getId(), connection));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Book save(Book book) {
        String sqlBook = "INSERT INTO lib.books (title, author_id) VALUES (?, ?) RETURNING id;";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBook, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setInt(2, book.getAuthorId());
                preparedStatement.executeUpdate();

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
            }

            updateBookGenres(book, connection);

            connection.commit();
            return book;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to save book", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Book book) {
        String sqlUpdateBook = "UPDATE lib.books SET title = ?, author_id = ? WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateBook)) {
                preparedStatement.setString(1, book.getTitle());
                preparedStatement.setInt(2, book.getAuthorId());
                preparedStatement.setInt(3, book.getId());
                preparedStatement.executeUpdate();
            }

            updateBookGenres(book, connection);

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to update book", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(int id) {
        String sqlDeleteBookGenres = "DELETE FROM lib.book_genre WHERE book_id = ?";
        String sqlDeleteBook = "DELETE FROM lib.books WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteBookGenres)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteBook)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to delete book", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBookGenres(Book book, Connection connection) throws SQLException {
        String sqlDeleteBookGenres = "DELETE FROM lib.book_genre WHERE book_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteBookGenres)) {
            preparedStatement.setInt(1, book.getId());
            preparedStatement.executeUpdate();
        }

        if (!book.getGenres().isEmpty()) {
            String sqlInsertBookGenre = "INSERT INTO lib.book_genre (book_id, genre_id) VALUES (?, ?);";
            for (Genre genre : book.getGenres()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertBookGenre)) {
                    preparedStatement.setInt(1, book.getId());
                    preparedStatement.setInt(2, genre.getId());
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    private List<Genre> getGenresForBook(int bookId, Connection connection) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT g.id, g.name FROM lib.genres g INNER JOIN lib.book_genre bg ON g.id = bg.genre_id WHERE bg.book_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Genre genre = new Genre();
                    genre.setId(resultSet.getInt("id"));
                    genre.setName(resultSet.getString("name"));
                    genres.add(genre);
                }
            }
        }
        return genres;
    }
}
