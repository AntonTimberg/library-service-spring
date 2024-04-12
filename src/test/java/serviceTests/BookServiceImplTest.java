package serviceTests;

import org.example.dao.BookDAO;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceImplTest {
    private BookDAO bookDAO;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookDAO = Mockito.mock(BookDAO.class);
        bookService = new BookServiceImpl(bookDAO);
    }

    @Test
    void findByIdWhenBookExists() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Book");
        book.setGenres(Collections.singletonList(new Genre("Genre")));
        when(bookDAO.findById(1)).thenReturn(Optional.of(book));

        Optional<Book> found = bookService.findById(1);

        assertEquals("Book", found.get().getTitle());
        verify(bookDAO).findById(1);
    }

    @Test
    void saveBookWithValidData() {
        Genre genre = new Genre("Fantasy");
        genre.setId(1);
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);

        Book book = new Book("New Book", 1);
        book.setGenres(genres);

        when(bookDAO.save(any(Book.class))).thenReturn(book);

        bookService.save(book);

        verify(bookDAO).save(book);
        assertEquals("New Book", book.getTitle());
        assertEquals(1, book.getGenres().get(0).getId());
    }

    @Test
    void saveBookWithEmptyTitle() {
        Book book = new Book();
        book.setGenres(Collections.singletonList(new Genre("Genre")));

        assertThrows(IllegalArgumentException.class, () -> bookService.save(book));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    void saveBookWithoutGenres() {
        Book book = new Book();
        book.setTitle("Book");

        assertThrows(IllegalArgumentException.class, () -> bookService.save(book));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    void updateValidBook() {
        Book book = new Book("Updated Book", 1);
        Genre genre = new Genre("Fantasy");
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        book.setGenres(genres);

        doNothing().when(bookDAO).update(book);

        bookService.update(book);

        verify(bookDAO).update(book);
    }

    @Test
    void updateBookWithEmptyTitle() {
        Book book = new Book("", 1);
        Genre genre = new Genre("Fantasy");
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);

        assertThrows(IllegalArgumentException.class, () -> bookService.update(book));
        verify(bookDAO, never()).update(any(Book.class));
    }

    @Test
    void updateBookWithoutGenres() {
        Book book = new Book("Updated Book", 1);
        List<Genre> genres = new ArrayList<>();

        book.setGenres(genres);

        assertThrows(IllegalArgumentException.class, () -> bookService.update(book));
        verify(bookDAO, never()).update(any(Book.class));
    }

    @Test
    void deleteBook() {
        int bookId = 1;
        doNothing().when(bookDAO).delete(bookId);

        bookService.delete(bookId);

        verify(bookDAO).delete(bookId);
    }
}
