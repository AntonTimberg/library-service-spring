package mapperTests;

import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GenreMapperTest {
    private GenreMapper genreMapper;

    @BeforeEach
    void setUp(){
        genreMapper = Mappers.getMapper(GenreMapper.class);
    }

    @Test
    void genreToDtoConvertTest() {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Фэнтези");

        GenreDTO genreDto = genreMapper.convert(genre);

        assertNotNull(genreDto);
        assertEquals(genre.getId(), genreDto.getId(), "ID должен соответствовать");
        assertEquals(genre.getName(), genreDto.getName(), "Название жанра должно соответствовать");
    }

    @Test
    void dtoToGeneConvertTest() {
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setId(1L);
        genreDTO.setName("Фэнтези");

        Genre genre = genreMapper.convert(genreDTO);

        assertNotNull(genre);
        assertEquals(genreDTO.getId(), genre.getId(), "ID должен соответствовать");
        assertEquals(genreDTO.getName(), genre.getName(), "Название жанра должно соответствовать");
    }

    @Test
    void nullGenreToDtoConvertTest() {
        assertNull(genreMapper.convert((Genre) null), "Преобразование null жанра должно возвращать null");
    }

    @Test
    void nullDtoToGenreConvertTest() {
        assertNull(genreMapper.convert((GenreDTO) null), "Преобразование null DTO должно возвращать null");
    }
}
