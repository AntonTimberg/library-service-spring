package repositoryTests;

import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
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
    void saveAndFindByIdTest() {
        Author author = new Author();
        author.setName("Толстой");

        authorRepository.save(author);

        var findAuthor = authorRepository.findById(author.getId());
        String name = findAuthor.get().getName();

        assertEquals("Толстой", name);
    }

    @Test
    void deleteAuthorTest() {
        Author author = new Author();
        author.setName("Толстой");

        Author savedAuthor = authorRepository.save(author);
        Long id = savedAuthor.getId();
        authorRepository.delete(savedAuthor);
        Optional<Author> foundAuthor = authorRepository.findById(id);

        assertFalse(foundAuthor.isPresent());
    }

    @Test
    void updateAuthorTest() {
        Author author = new Author();
        author.setName("Толстой");

        Author savedAuthor = authorRepository.save(author);
        savedAuthor.setName("Лев Толстой");
        authorRepository.save(savedAuthor);
        Optional<Author> updatedAuthor = authorRepository.findById(savedAuthor.getId());

        assertEquals("Лев Толстой", updatedAuthor.get().getName());
    }

    @Test
    void findAllAuthorsTest() {
        Author author = new Author();
        Author author2 = new Author();
        author.setName("Пушкин");
        author2.setName("Толстой");
        authorRepository.save(author);
        authorRepository.save(author2);

        List<String> genreNames = authorRepository.findAll().stream()
                .map(Author::getName)
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Пушкин", "Толстой");
        Assert.assertEquals(expectedNames, genreNames);
    }
}
