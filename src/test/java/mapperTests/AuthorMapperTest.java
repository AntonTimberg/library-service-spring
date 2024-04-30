package mapperTests;

import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void authorEmptyNameConvertTest() {
        Author author = new Author();
        author.setId(1L);
        author.setName("");

        AuthorDTO authorDTO = authorMapper.convert(author);

        assertNotNull(authorDTO);
        assertEquals("", authorDTO.getName());
    }

    @Test
    void dtoToAuthorWithEmptyNameConvertTest() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1);
        authorDTO.setName("");

        Author author = authorMapper.convert(authorDTO);

        assertNotNull(author);
        assertEquals("", author.getName());
    }

    @Test
    void nullAuthorConvertTest() {
        assertNull(authorMapper.convert((Author) null), "Конвертация null автора должна возвращать null");
    }

    @Test
    void nullDtoToAuthorConvertTest() {
        assertNull(authorMapper.convert((AuthorDTO) null), "Конвертация null DTO должна возвращать null");
    }
}
