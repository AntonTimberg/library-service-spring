package serviceTests;

import org.example.dao.AuthorDAO;
import org.example.model.Author;
import org.example.service.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {
    private AuthorDAO authorDAO;
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        authorDAO = Mockito.mock(AuthorDAO.class);
        authorService = new AuthorServiceImpl(authorDAO);
    }

    @Test
    void findByIdWhenAuthorExists() {
        Author author = new Author("Тестовый");
        when(authorDAO.findById(1)).thenReturn(Optional.of(author));

        Optional<Author> found = authorService.findById(1);

        assertEquals("Тестовый", found.get().getName());
        verify(authorDAO).findById(1);
    }

    @Test
    void updateValidAuthor() {
        Author author = new Author("Обновлённый Автор");
        author.setId(1);
        doNothing().when(authorDAO).update(author);

        authorService.update(author);

        verify(authorDAO).update(author);
    }

    @Test
    void updateInvalidAuthor() {
        Author invalidAuthor = new Author("");
        invalidAuthor.setId(1);

        assertThrows(IllegalArgumentException.class, () -> authorService.update(invalidAuthor));
        verify(authorDAO, never()).update(any(Author.class));
    }

    @Test
    void deleteAuthor() {
        doNothing().when(authorDAO).delete(1);
        authorService.delete(1);

        verify(authorDAO).delete(1);
    }

    @Test
    void saveInvalidAuthor() {
        Author author = new Author("");

        assertThrows(IllegalArgumentException.class, () -> authorService.save(author));
        verify(authorDAO, never()).save(any(Author.class));
    }
}
