package repositoryTests;

import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
@Testcontainers
@Transactional
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void saveAndFindGenreTest() {
        Genre genre = new Genre();
        genre.setName("Fantasy");
        Genre savedGenre = genreRepository.save(genre);
        assertNotNull(savedGenre);
        assertEquals("Fantasy", genreRepository.findById(savedGenre.getId()).get().getName());
    }

    @Test
    void deleteGenreTest() {
        Genre genre = new Genre();
        genre.setName("Fantasy");
        Genre savedGenre = genreRepository.save(genre);

        genreRepository.deleteById(savedGenre.getId());
        assertFalse(genreRepository.findById(savedGenre.getId()).isPresent());
    }

    @Test
    public void updateGenreTest() {
        Genre genre = new Genre();
        genre.setName("Фентези");

        Genre savedGenre = genreRepository.save(genre);
        savedGenre.setName("Не Фентези");
        genreRepository.save(savedGenre);
        Optional<Genre> updatedGenre = genreRepository.findById(savedGenre.getId());

        assertEquals("Не Фентези", updatedGenre.get().getName());
    }

    @Test
    void findAllGenresTest() {
        Genre genre1 = new Genre();
        genre1.setName("Adventure");
        genreRepository.save(genre1);
        Genre genre2 = new Genre();
        genre2.setName("Mystery");
        genreRepository.save(genre2);

        List<String> genreNames = genreRepository.findAll().stream()
                .map(Genre::getName)
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Adventure", "Mystery");
        assertEquals(expectedNames, genreNames);
    }
}
