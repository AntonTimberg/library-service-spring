package repositoryTests;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
@Testcontainers
@Transactional
class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testFindById() {
        Author author = new Author();
        author.setName("Толстой");

        authorRepository.save(author);

        var findAuthor = authorRepository.findById(author.getId());
        String name = findAuthor.get().getName();

        assertEquals("Толстой", name);
    }

    @Test
    public void testDeleteAuthor() {
        Author author = new Author();
        author.setName("Толстой");

        Author savedAuthor = authorRepository.save(author);
        Long id = savedAuthor.getId();
        authorRepository.delete(savedAuthor);
        Optional<Author> foundAuthor = authorRepository.findById(id);

        assertFalse(foundAuthor.isPresent());
    }

    @Test
    public void testUpdateAuthor() {
        Author author = new Author();
        author.setName("Толстой");

        Author savedAuthor = authorRepository.save(author);
        savedAuthor.setName("Лев Толстой");
        authorRepository.save(savedAuthor);
        Optional<Author> updatedAuthor = authorRepository.findById(savedAuthor.getId());

        assertEquals("Лев Толстой", updatedAuthor.get().getName());
    }

    @Test
    @Transactional
    public void testCascadeDeleteBooks() {
        Author author = new Author();
        author.setName("Толстой");
        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthor(author);
        //book.setGenres();

        authorRepository.save(author);
        bookRepository.save(book);
        authorRepository.delete(author);

        assertTrue(bookRepository.findById(book.getId()).isEmpty());
    }
}
