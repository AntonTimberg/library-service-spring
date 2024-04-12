package serviceTests;

import org.example.dao.GenreDAO;
import org.example.model.Genre;
import org.example.service.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreServiceImplTest {
    private GenreDAO genreDAO;
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        genreDAO = Mockito.mock(GenreDAO.class);
        genreService = new GenreServiceImpl(genreDAO);
    }

    @Test
    void findByIdWhenGenreExists() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Fantasy");
        when(genreDAO.findById(1)).thenReturn(Optional.of(genre));

        Optional<Genre> found = genreService.findById(1);

        assertEquals("Fantasy", found.get().getName());
        verify(genreDAO).findById(1);
    }

    @Test
    void findAllGenres() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Fantasy");
        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Sci-Fi");
        List<Genre> expectedGenres = Arrays.asList(genre1, genre2);
        when(genreDAO.findAll()).thenReturn(expectedGenres);

        List<Genre> genres = genreService.findAll();

        assertEquals(expectedGenres, genres);
        verify(genreDAO).findAll();
    }

    @Test
    void saveGenre() {
        Genre genre = new Genre();
        genre.setName("Pulp fiction");
        doNothing().when(genreDAO).save(any(Genre.class));

        genreService.save(genre);

        verify(genreDAO).save(genre);
    }

    @Test
    void saveGenreWithEmptyName() {
        Genre genre = new Genre();
        genre.setName("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> genreService.save(genre));
        assertEquals("Название жанра, не может быть пустым.", exception.getMessage());
    }

    @Test
    void updateGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Ужасы");
        doNothing().when(genreDAO).update(any(Genre.class));

        genreService.update(genre);

        verify(genreDAO).update(genre);
    }

    @Test
    void updateGenreWithNullName() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> genreService.update(genre));
        assertEquals("Название жанра, не может быть пустым.", exception.getMessage());
    }

    @Test
    void deleteGenre() {
        int genreId = 1;
        doNothing().when(genreDAO).delete(genreId);

        genreService.delete(genreId);

        verify(genreDAO).delete(genreId);
    }
}
