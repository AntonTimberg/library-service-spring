package serviceTests;

import jakarta.persistence.EntityNotFoundException;
import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.service.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AuthorServiceImplTest {
    @InjectMocks
    private AuthorServiceImpl authorService;
    @Mock
    private AuthorRepository authorRepo;
    @Mock
    private AuthorMapper authorMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllTest(){
        var author = new Author();
        author.setName("Лев Толстой");
        var authorDTO = new AuthorDTO();
        authorDTO.setName("Лев Толстой");
        List<Author> authors = List.of(author);
        List<AuthorDTO> authorDTOS = List.of(authorDTO);

        when(authorRepo.findAll()).thenReturn(authors);
        when(authorMapper.convert(author)).thenReturn(authorDTO);

        var actual = authorService.findAll();

        assertEquals(authorDTOS, actual);

        verify(authorRepo).findAll();
        verify(authorMapper).convert(author);
        verifyNoMoreInteractions(authorRepo, authorMapper);
     }

    @Test
    void findByIdTest(){
        var authorID = 1L;
        var author = new Author();
        author.setId(authorID);
        author.setName("Лев Толстой");
        var authorDTO = new AuthorDTO();
        authorDTO.setId((int) authorID);
        authorDTO.setName("Лев Толстой");

        when(authorRepo.findById(authorID)).thenReturn(Optional.of(author));
        when(authorMapper.convert(author)).thenReturn(authorDTO);

        var actual = authorService.findById(authorID);

        assertEquals(authorDTO, actual);

        verify(authorRepo).findById(authorID);
        verify(authorMapper).convert(author);
        verifyNoMoreInteractions(authorRepo, authorMapper);
    }

    @Test
    void saveAuthorTest(){
        var author = new Author();
        author.setName("Лев Толстой");
        var savedAuthor = new Author();
        savedAuthor.setName("Лев Толстой");
        var convertedAuthor = new AuthorDTO();
        convertedAuthor.setName("Лев Толстой");

        when(authorRepo.save(author)).thenReturn(savedAuthor);
        when(authorMapper.convert(savedAuthor)).thenReturn(convertedAuthor);

        var actual = authorService.save(author);

        assertEquals(actual, convertedAuthor);
        verify(authorRepo).save(author);
        verify(authorMapper).convert(savedAuthor);
        verifyNoMoreInteractions(authorRepo);
        verifyNoMoreInteractions(authorMapper);
    }

    @Test
    void updateAuthorTest(){
        var authorID = 1L;
        var author = new Author();
        author.setId(authorID);
        author.setName("Лев Толстой");
        var authorDTO = new AuthorDTO();
        authorDTO.setId((int) authorID);
        authorDTO.setName("Лев Толстой");

        when(authorRepo.existsById(author.getId())).thenReturn(true);
        when(authorRepo.save(author)).thenReturn(author);
        when(authorMapper.convert(author)).thenReturn(authorDTO);

        AuthorDTO actual = authorService.update(author);

        assertEquals(authorDTO, actual);

        verify(authorRepo).existsById(author.getId());
        verify(authorRepo).save(author);
        verify(authorMapper).convert(author);

        verifyNoMoreInteractions(authorRepo, authorMapper);
    }

    @Test
    void deleteAuthorWhenAuthorIsFoundTest(){
        var authorID = 1L;
        var author = new Author();
        author.setName("Лев Толстой");

        when(authorRepo.findById(authorID)).thenReturn(Optional.of(author));

        authorService.delete(authorID);

        verify(authorRepo).findById(authorID);
        verify(authorRepo).deleteById(authorID);
        verifyNoMoreInteractions(authorRepo);
    }

    @Test
    void deleteAuthorWhenAuthorIsNullTest(){
        var authorID = 1L;

        when(authorRepo.findById(authorID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            authorService.delete(authorID);
        });
        String expectedMessage = "Автор с id=" + authorID + " не найден";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(authorRepo).findById(authorID);
        verifyNoMoreInteractions(authorRepo);
    }
}
