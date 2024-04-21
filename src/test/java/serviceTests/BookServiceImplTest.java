package serviceTests;

import jakarta.persistence.EntityNotFoundException;
import org.example.controller.dto.BookDTO;
import org.example.controller.dto.mapper.BookMapper;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepo;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private AuthorRepository authorRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllBooksTest() {
        Book book = new Book();
        book.setTitle("Синий трактор");
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Синий трактор");
        List<Book> books = List.of(book);
        List<BookDTO> bookDTOs = List.of(bookDTO);

        when(bookRepo.findAll()).thenReturn(books);
        when(bookMapper.convert(book)).thenReturn(bookDTO);

        List<BookDTO> actual = bookService.findAll();

        assertEquals(bookDTOs, actual);
        verify(bookRepo).findAll();
        verify(bookMapper).convert(book);
        verifyNoMoreInteractions(bookRepo, bookMapper);
    }

    @Test
    void findBookByIdTest() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Синий трактор");
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Синий трактор");

        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.convert(book)).thenReturn(bookDTO);

        BookDTO actual = bookService.findById(bookId);

        assertEquals(bookDTO, actual);
        verify(bookRepo).findById(bookId);
        verify(bookMapper).convert(book);
        verifyNoMoreInteractions(bookRepo, bookMapper);
    }

    @Test
    void findBookByNotExistedIDTest() {
        Long nonExistentId = 1L;
        when(bookRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.findById(nonExistentId);
        });

        assertEquals("Книга с id " + nonExistentId + " не найдена", exception.getMessage());
        verify(bookRepo).findById(nonExistentId);
        verifyNoMoreInteractions(bookRepo, bookMapper);
    }

    @Test
    void saveBookTest() {
        Book book = new Book();
        book.setTitle("Возвышение Хоруса");
        Author author = new Author();
        author.setId(1L);
        book.setAuthor(author);
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Возвышение Хоруса");

        when(authorRepo.findById(author.getId())).thenReturn(Optional.of(author));
        when(bookRepo.save(book)).thenReturn(book);
        when(bookMapper.convert(book)).thenReturn(bookDTO);

        BookDTO actual = bookService.save(book);

        assertEquals(bookDTO, actual);
        verify(authorRepo).findById(author.getId());
        verify(bookRepo).save(book);
        verify(bookMapper).convert(book);
        verifyNoMoreInteractions(authorRepo, bookRepo, bookMapper);
    }

    @Test
    void saveBookWithoutTitleTest() {
        Book book = new Book();
        book.setTitle("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(book);
        });

        assertEquals("Название книги не может быть пустым", exception.getMessage());
        verifyNoInteractions(bookRepo, bookMapper, authorRepo);
    }

    @Test
    void saveBookWithoutAuthorTest() {
        Book bookWithoutAuthor = new Book();
        bookWithoutAuthor.setTitle("Последний страж");
        bookWithoutAuthor.setAuthor(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(bookWithoutAuthor);
        });

        assertEquals("Книга должна иметь автора.", exception.getMessage());

        verifyNoInteractions(bookRepo);
    }

    @Test
    void saveBookWithNonExistAuthorIdTest() {
        Long nonExistAuthorId = 1L;
        Book book = new Book();
        book.setTitle("Последний страж");
        Author author = new Author();
        author.setId(nonExistAuthorId);
        book.setAuthor(author);

        when(authorRepo.findById(nonExistAuthorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.save(book);
        });

        assertEquals("Автор книги с id=" + nonExistAuthorId + " не найден.", exception.getMessage());

        verify(authorRepo).findById(nonExistAuthorId);
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void updateBookTest() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Возвышение Хоруса");
        Author author = new Author();
        author.setId(1L);
        book.setAuthor(author);
        Book existingBook = new Book();
        existingBook.setId(book.getId());
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(author);

        when(authorRepo.findById(author.getId())).thenReturn(Optional.of(author));
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepo.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.convert(existingBook)).thenReturn(new BookDTO());

        bookService.update(book);

        verify(bookRepo).findById(bookId);
        verify(authorRepo).findById(author.getId());
        verify(bookRepo).save(existingBook);
        verify(bookMapper).convert(existingBook);
        verifyNoMoreInteractions(bookRepo, bookMapper, authorRepo);
    }

    @Test
    void deleteBookWhenBookIsFoundTest() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));

        bookService.delete(bookId);

        verify(bookRepo).findById(bookId);
        verify(bookRepo).deleteById(bookId);
        verifyNoMoreInteractions(bookRepo);
    }

    @Test
    void deleteBookWhenBookIsNullTest() {
        Long bookId = 1L;
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.delete(bookId);
        });

        assertEquals("Книга с id=" + bookId + " не найдена", exception.getMessage());
        verify(bookRepo).findById(bookId);
        verifyNoMoreInteractions(bookRepo);
    }
}
