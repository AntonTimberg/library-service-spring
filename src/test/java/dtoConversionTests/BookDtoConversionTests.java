package dtoConversionTests;

import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.example.servlet.dto.BookDTO;
import org.example.servlet.dto.converter.BookDtoToBookConverter;
import org.example.servlet.dto.converter.BookToBookDtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookDtoConversionTests {
    private GenreService genreService;
    private BookDtoToBookConverter converter;

    @BeforeEach
    void setUp() {
        genreService = mock(GenreService.class);
        converter = new BookDtoToBookConverter(genreService);
    }

    @Test
    void dtoToBookReturnGenre() {
        BookDTO dto = new BookDTO(1, "Book", 1, Arrays.asList(1, 2));
        when(genreService.findById(1)).thenReturn(Optional.of(new Genre("Genre")));

        Book book = converter.convert(dto);

        assertEquals("Genre", book.getGenres().get(0).getName());
    }

    @Test
    void bookConvertToDTOReturnGenres() {
        Book book = new Book("Book", 1);
        book.setId(1);
        Genre genre1 = new Genre();
        genre1.setId(1);
        Genre genre2 = new Genre();
        genre2.setId(2);
        book.setGenres(Arrays.asList(genre1, genre2));

        BookDTO dto = new BookToBookDtoConverter().convert(book);

        assertEquals(Arrays.asList(1, 2), dto.getGenreIds());
    }

    @Test
    void bookConvertToDTOCheckName() {
        Book book = new Book("Book", 1);
        book.setId(1);
        Genre genre1 = new Genre();
        genre1.setId(1);
        Genre genre2 = new Genre();
        genre2.setId(2);
        book.setGenres(Arrays.asList(genre1, genre2));

        BookDTO dto = new BookToBookDtoConverter().convert(book);

        assertEquals("Book", dto.getTitle());
    }
}
