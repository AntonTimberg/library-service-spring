package dtoConversionTests;

import org.example.model.Genre;
import org.example.servlet.dto.GenreDTO;
import org.example.servlet.dto.converter.GenreDtoToGenreConverter;
import org.example.servlet.dto.converter.GenreToGenreDtoConverter;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;

class GenreDtoConversionTests {
    @Test
    void genreToDtoNameTest() {
        Genre genre = new Genre("Fantasy");
        genre.setId(1);
        GenreToGenreDtoConverter converter = new GenreToGenreDtoConverter();

        GenreDTO dto = converter.convert(genre);

        assertEquals(genre.getName(), dto.getName());
    }

    @Test
    void genreToDtoIDTest() {
        Genre genre = new Genre("Fantasy");
        genre.setId(1);
        GenreToGenreDtoConverter converter = new GenreToGenreDtoConverter();

        GenreDTO dto = converter.convert(genre);

        assertEquals(genre.getId(), dto.getId());
    }

    @Test
    void dtoToGenreNameTest() {
        GenreDTO dto = new GenreDTO(1, "Fantasy");
        GenreDtoToGenreConverter converter = new GenreDtoToGenreConverter();

        Genre genre = converter.convert(dto);

        assertEquals(dto.getName(), genre.getName());
    }

    @Test
    void dtoToGenreIDTest() {
        GenreDTO dto = new GenreDTO(1, "Fantasy");
        GenreDtoToGenreConverter converter = new GenreDtoToGenreConverter();

        Genre genre = converter.convert(dto);

        assertEquals(dto.getId(), genre.getId());
    }
}
