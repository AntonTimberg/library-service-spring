package dtoConversionTests;

import org.example.model.Author;
import org.example.model.Book;
import org.example.servlet.dto.AuthorDTO;
import org.example.servlet.dto.converter.AuthorDtoToAuthorConverter;
import org.example.servlet.dto.converter.AuthorToAuthorDtoConverter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

class AuthorDtoConversionTests {
    @Test
    void convertAuthorDtoToAuthorNameTest() {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(1);
        dto.setName("Vasiliy");

        Author author = new AuthorDtoToAuthorConverter().convert(dto);

        assertEquals(dto.getName(), author.getName());
    }

    @Test
    void convertAuthorDtoToAuthorIdTest() {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(1);
        dto.setName("Vasiliy");

        Author author = new AuthorDtoToAuthorConverter().convert(dto);

        assertEquals(dto.getId(), author.getId());
    }

    @Test
    void convertAuthorToAuthorDtoBooksTest() {
        Author author = new Author();
        author.setId(1);
        author.setName("Vasiliy");
        Book book1 = new Book("Book One", 1);
        book1.setId(1);
        Book book2 = new Book("Book Two", 1);
        book2.setId(2);
        author.addBook(book1);
        author.addBook(book2);

        AuthorDTO dto = new AuthorToAuthorDtoConverter().convert(author);

        assertTrue(dto.getBookIds().containsAll(Arrays.asList(1, 2)));
    }

    @Test
    void convertAuthorToAuthorDtoNameTest() {
        Author author = new Author();
        author.setId(1);
        author.setName("Vasiliy");

        AuthorDTO dto = new AuthorToAuthorDtoConverter().convert(author);

        assertEquals("Vasiliy", dto.getName());
    }
}
