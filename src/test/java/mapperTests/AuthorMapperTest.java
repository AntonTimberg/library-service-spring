package mapperTests;

import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorMapperTest {
    private AuthorMapper authorMapper;

    @BeforeEach
    void setup() {
        authorMapper = Mappers.getMapper(AuthorMapper.class);
    }

    @Test
    void authorToDtoNameTest() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Лев Толстой");

        AuthorDTO authorDTO = authorMapper.convert(author);

        assertEquals(author.getName(), authorDTO.getName());
    }

    @Test
    void authorToDtoIdTest() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Лев Толстой");

        AuthorDTO authorDTO = authorMapper.convert(author);

        assertEquals(author.getId(), authorDTO.getId());
    }

    @Test
    void dtoToAuthorNameTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1);
        authorDTO.setName("Лев Толстой");

        Author author = authorMapper.convert(authorDTO);

        assertEquals(authorDTO.getName(), author.getName());
    }

    @Test
    void dtoToAuthorIdTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1);
        authorDTO.setName("Лев Толстой");

        Author author = authorMapper.convert(authorDTO);

        assertEquals(authorDTO.getId(), author.getId());
    }
}
