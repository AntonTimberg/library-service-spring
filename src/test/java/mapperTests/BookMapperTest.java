package mapperTests;

import org.example.controller.dto.BookDTO;
import org.example.controller.dto.mapper.BookMapper;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BookMapperTest {
    private BookMapper bookMapper;

    @Mock
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookMapper = Mappers.getMapper(BookMapper.class);
    }

    @Test
    void bookToDTONameTest() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Book book = new Book("Война и мир", author);
        book.setId(1L);
        book.setGenres(genres);

        when(genreService.findAllByIds(List.of(1L, 2L))).thenReturn(new HashSet<>(genres));

        BookDTO bookDTO = bookMapper.convert(book);
        assertEquals(book.getTitle(), bookDTO.getTitle());
    }

    @Test
    void bookToDTOIdTest() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Book book = new Book("Война и мир", author);
        book.setId(1L);
        book.setGenres(genres);

        when(genreService.findAllByIds(List.of(1L, 2L))).thenReturn(new HashSet<>(genres));

        BookDTO bookDTO = bookMapper.convert(book);
        assertEquals(book.getId(), bookDTO.getId());
    }

    @Test
    void bookToDTOAuthorTest() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Book book = new Book("Война и мир", author);
        book.setId(1L);
        book.setGenres(genres);

        when(genreService.findAllByIds(List.of(1L, 2L))).thenReturn(new HashSet<>(genres));

        BookDTO bookDTO = bookMapper.convert(book);
        assertEquals(book.getAuthor().getId(), bookDTO.getAuthorId());
    }

    @Test
    void bookToDTOGenreQuantityTest() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Book book = new Book("Война и мир", author);
        book.setId(1L);
        book.setGenres(genres);

        when(genreService.findAllByIds(List.of(1L, 2L))).thenReturn(new HashSet<>(genres));

        BookDTO bookDTO = bookMapper.convert(book);
        assertEquals(2, bookDTO.getGenreIds().size());
    }

    @Test
    void dtoToBookNameTest() {
        BookDTO bookDTO = new BookDTO(1L, "Война и мир", 1L, Arrays.asList(1L, 2L));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        when(genreService.findAllByIds(bookDTO.getGenreIds())).thenReturn(genres);

        Book book = bookMapper.convert(bookDTO, genreService);
        assertEquals(bookDTO.getTitle(), book.getTitle());
    }

    @Test
    void dtoToBookIdTest() {
        BookDTO bookDTO = new BookDTO(1L, "Война и мир", 1L, Arrays.asList(1L, 2L));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        when(genreService.findAllByIds(bookDTO.getGenreIds())).thenReturn(genres);

        Book book = bookMapper.convert(bookDTO, genreService);
        assertEquals(bookDTO.getId(), book.getId());
    }

    @Test
    void dtoToBookIdTestAuthorTest() {
        BookDTO bookDTO = new BookDTO(1L, "Война и мир", 1L, Arrays.asList(1L, 2L));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        when(genreService.findAllByIds(bookDTO.getGenreIds())).thenReturn(genres);

        Book book = bookMapper.convert(bookDTO, genreService);
        assertEquals(bookDTO.getAuthorId(), book.getAuthor().getId());
    }

    @Test
    void dtoToBookIdTestGenresQuantityTest() {
        BookDTO bookDTO = new BookDTO(1L, "Война и мир", 1L, Arrays.asList(1L, 2L));

        Author author = new Author("Лев Толстой");
        author.setId(1L);

        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>(Arrays.asList(genre1, genre2));

        when(genreService.findAllByIds(bookDTO.getGenreIds())).thenReturn(genres);

        Book book = bookMapper.convert(bookDTO, genreService);
        assertEquals(2, book.getGenres().size());
    }
}
