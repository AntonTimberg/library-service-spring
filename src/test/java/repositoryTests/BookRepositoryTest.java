package repositoryTests;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
@Testcontainers
@Transactional
class BookRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void saveAndFindBookTest() {
        Author author = new Author();
        author.setName("Лев Толстой");
        authorRepository.save(author);

        Genre genre = new Genre();
        genre.setName("Роман");
        genreRepository.save(genre);

        Book book = new Book();
        book.setAuthor(authorRepository.findById(author.getId()).get());
        book.setTitle("Война и мир");
        Set<Genre> genreList = new HashSet<>();
        genreList.add(genreRepository.findById(genre.getId()).get());
        book.setGenres(genreList);

        bookRepository.save(book);

        assertEquals("Война и мир", bookRepository.findById(1L).get().getTitle());
    }

    @Test
    void deleteBookTest() {
        Author author = new Author();
        author.setName("Лев Толстой");
        authorRepository.save(author);

        Genre genre = new Genre();
        genre.setName("Роман");
        genreRepository.save(genre);

        Book book = new Book();
        book.setAuthor(authorRepository.findById(author.getId()).get());
        book.setTitle("Война и мир");
        Set<Genre> genreList = new HashSet<>();
        genreList.add(genreRepository.findById(genre.getId()).get());
        book.setGenres(genreList);

        bookRepository.save(book);

        bookRepository.deleteById(book.getId());

        assertTrue(bookRepository.findById(book.getId()).isEmpty());
    }

    @Test
    void updateBookTest() {
        Author author = new Author();
        author.setName("Лев Толстой");
        authorRepository.save(author);

        Genre genre = new Genre();
        genre.setName("Роман");
        genreRepository.save(genre);

        Book book = new Book();
        book.setAuthor(authorRepository.findById(author.getId()).get());
        book.setTitle("Война и мир");
        Set<Genre> genreList = new HashSet<>();
        genreList.add(genreRepository.findById(genre.getId()).get());
        book.setGenres(genreList);

        bookRepository.save(book);
        book.setTitle("Новый век");
        bookRepository.save(book);

        assertEquals("Новый век", bookRepository.findById(book.getId()).get().getTitle());
    }

    @Test
    void findAllBooksTest() {
        Author author = new Author();
        author.setName("Лев Толстой");
        authorRepository.save(author);

        Genre genre = new Genre();
        genre.setName("Роман");
        genreRepository.save(genre);

        Book book = new Book();
        book.setAuthor(authorRepository.findById(author.getId()).get());
        book.setTitle("Война и мир");
        Set<Genre> genreList = new HashSet<>();
        genreList.add(genreRepository.findById(genre.getId()).get());
        book.setGenres(genreList);

        Book book2 = new Book();
        book2.setAuthor(authorRepository.findById(author.getId()).get());
        book2.setTitle("Новый век");
        book2.setGenres(genreList);

        bookRepository.save(book);
        bookRepository.save(book2);

        List<String> bookNames = bookRepository.findAll().stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
        List<String> expectedNames = Arrays.asList("Война и мир", "Новый век");
        Assertions.assertEquals(expectedNames, bookNames);
    }
}
