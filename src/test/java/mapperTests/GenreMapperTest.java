package mapperTests;

import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenreMapperTest {
    private GenreMapper genreMapper;

    @BeforeEach
    void setUp(){
        genreMapper = Mappers.getMapper(GenreMapper.class);
    }

    @Test
    void genreToDtoNameTest(){
        Genre genre = new Genre();
        genre.setName("Жанр");
        genre.setId(1L);

        var genreDto = genreMapper.convert(genre);

        assertEquals(genre.getName(), genreDto.getName());
    }

    @Test
    void dtoToGenreNameTest(){
        var genreDTO = new GenreDTO();
        genreDTO.setId(1L);
        genreDTO.setName("Жанр");

        var genre = genreMapper.convert(genreDTO);

        assertEquals(genre.getName(), genreDTO.getName());
    }

    @Test
    void genreToDtoIdTest(){
        Genre genre = new Genre();
        genre.setName("Жанр");
        genre.setId(1L);

        var genreDto = genreMapper.convert(genre);

        assertEquals(genre.getName(), genreDto.getName());
    }

    @Test
    void dtoToGenreIdTest(){
        var genreDTO = new GenreDTO();
        genreDTO.setId(1L);
        genreDTO.setName("Жанр");

        var genre = genreMapper.convert(genreDTO);

        assertEquals(genre.getId(), genreDTO.getId());
    }
}
