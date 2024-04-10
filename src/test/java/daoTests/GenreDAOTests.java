package daoTests;

import org.example.dao.GenreDAO;
import org.example.dao.GenreDAOImpl;
import org.example.model.Genre;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

class GenreDAOTests {
    private GenreDAO genreDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        TestDBServant.initializeTestDB();
        genreDAO = new GenreDAOImpl();
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
    void addingSevenGenresAndDeleteTwo() {
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
    void genreFindAll() {
        for (int i = 0; i < 10; i++) {
            Genre genre = new Genre();
            genre.setName("Sci-Fi " + i);
            genreDAO.save(genre);
        }

        assertEquals(10, genreDAO.findAll().size());
    }

    @Test
    void addGenreAndDelete() {
        Genre genre = new Genre();
        genre.setName("Sci-Fi");
        genreDAO.save(genre);

        genreDAO.delete(1);

        assertEquals(0, genreDAO.findAll().size());
    }
}
