package serviceTests;

import org.example.dao.BookDAO;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServiceImplTest {
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
}
