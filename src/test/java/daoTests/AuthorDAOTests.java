package daoTests;

import org.example.dao.AuthorDAO;
import org.example.dao.AuthorDAOImpl;
import org.example.model.Author;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuthorDAOTests {
    private AuthorDAO authorDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        TestDBServant.initializeTestDB();
        authorDAO = new AuthorDAOImpl();
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
    void addingAndDeletingAuthors() {
        authorDAO.save(new Author("Первый"));
        authorDAO.save(new Author("Второй"));
        authorDAO.save(new Author("Третий"));
        authorDAO.save(new Author("Четвертый"));
        authorDAO.save(new Author("Пятый"));
        authorDAO.save(new Author("Шестой"));
        authorDAO.save(new Author("Седьмой"));

        Author authorToDelete = authorDAO.findAll().get(4);
        authorDAO.delete(authorToDelete.getId());
        authorToDelete = authorDAO.findAll().get(5);
        authorDAO.delete(authorToDelete.getId());

        assertEquals(5, authorDAO.findAll().size());
    }

    @Test
    void authorAddingAndFind() {
        authorDAO.save(new Author("Первый"));
        assertEquals("Первый", authorDAO.findById(1).get().getName());
    }

    @Test
    void authorAddingAndUpdate() {
        authorDAO.save(new Author("Первый"));
        Author author = authorDAO.findAll().get(0);
        author.setName("Последний");
        authorDAO.update(author);

        assertEquals("Последний", authorDAO.findById(author.getId()).get().getName());
    }

    @Test
    void deleteAuthor() {
        Author author = new Author("Удаляемый");
        authorDAO.save(author);

        authorDAO.delete(author.getId());
        Optional<Author> deletedAuthor = authorDAO.findById(author.getId());

        assertFalse(deletedAuthor.isPresent(), "Автор не был удален.");
    }
}
