package org.example.dao;

import org.example.DBConnection;
import org.example.model.Author;
import org.example.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorDAOImpl implements AuthorDAO {
    @Override
    public Optional<Author> findById(int id) {
        String sqlAuthor = "SELECT * FROM lib.authors WHERE id = ?";
        Author author = null;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement psAuthor = connection.prepareStatement(sqlAuthor)) {

            psAuthor.setInt(1, id);
            ResultSet rsAuthor = psAuthor.executeQuery();

            if (rsAuthor.next()) {
                author = new Author();
                author.setId(rsAuthor.getInt("id"));
                author.setName(rsAuthor.getString("name"));

                author.setBooks(getBooksForAuthor(id, connection));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.ofNullable(author);
    }

    @Override
    public List<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM lib.authors";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Author author = new Author();
                author.setId(resultSet.getInt("id"));
                author.setName(resultSet.getString("name"));

                author.setBooks(getBooksForAuthor(author.getId(), connection));

                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private List<Book> getBooksForAuthor(int authorId, Connection connection) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sqlBooks = "SELECT * FROM lib.books WHERE author_id = ?";
        try (PreparedStatement psBooks = connection.prepareStatement(sqlBooks)) {
            psBooks.setInt(1, authorId);
            ResultSet rsBooks = psBooks.executeQuery();
            while (rsBooks.next()) {
                Book book = new Book();
                book.setId(rsBooks.getInt("id"));
                book.setTitle(rsBooks.getString("title"));
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public void save(Author author) {
        String sql = "INSERT INTO lib.authors (name) VALUES (?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, author.getName());
                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to save author", e);
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
    public void update(Author author) {
        String sql = "UPDATE lib.authors SET name = ? WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, author.getName());
                preparedStatement.setInt(2, author.getId());
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
            throw new RuntimeException("Failed to update author", e);
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
        String sql = "DELETE FROM lib.authors WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
            throw new RuntimeException("Failed to delete author", e);
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
}
