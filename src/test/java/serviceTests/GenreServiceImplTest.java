package serviceTests;

import jakarta.persistence.EntityNotFoundException;
import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.example.service.GenreServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class GenreServiceImplTest {
    @InjectMocks
    private GenreServiceImpl genreService;
    @Mock
    private GenreMapper genreMapper;
    @Mock
    private GenreRepository genreRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findGenreByIdTest() {
        Long genreId = 1L;
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName("Фантастика");
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setId(genreId);
        genreDTO.setName("Фантастика");

        when(genreRepo.findById(genreId)).thenReturn(Optional.of(genre));
        when(genreMapper.convert(genre)).thenReturn(genreDTO);

        GenreDTO actual = genreService.findById(genreId);

        assertEquals(genreDTO, actual);
        verify(genreRepo).findById(genreId);
        verify(genreMapper).convert(genre);
        verifyNoMoreInteractions(genreRepo, genreMapper);
    }

    @Test
    void findGenreWithNoExistedIdTest() {
        Long nonExistentId = 1L;
        when(genreRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            genreService.findById(nonExistentId);
        });

        assertEquals("Жанр с id " + nonExistentId + " не найден.", exception.getMessage());
        verify(genreRepo).findById(nonExistentId);
        verifyNoMoreInteractions(genreRepo, genreMapper);
    }

    @Test
    void findAllGenresTest() {
        Genre genre = new Genre();
        genre.setName("Фантастика");
        List<Genre> genres = List.of(genre);
        List<GenreDTO> genreDTOs = List.of(new GenreDTO(genre.getId(), genre.getName()));

        when(genreRepo.findAll()).thenReturn(genres);
        when(genreMapper.convert(genre)).thenReturn(genreDTOs.get(0));

        List<GenreDTO> actual = genreService.findAll();

        assertEquals(genreDTOs, actual);
        verify(genreRepo).findAll();
        verify(genreMapper).convert(genre);
        verifyNoMoreInteractions(genreRepo, genreMapper);
    }

    @Test
    void saveGenreWithExistingNameTest() {
        Genre genre = new Genre();
        genre.setName("Фэнтези");

        when(genreRepo.findAll()).thenReturn(List.of(genre));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            genreService.save(genre);
        });

        assertEquals("Жанр с именем Фэнтези уже существует.", exception.getMessage());
        verify(genreRepo).findAll();
        verifyNoMoreInteractions(genreRepo, genreMapper);
    }

    @Test
    void saveGenreWithoutNameTest() {
        Genre genre = new Genre();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            genreService.save(genre);
        });

        assertEquals("Название жанра, не может быть пустым.", exception.getMessage());
        verifyNoInteractions(genreRepo, genreMapper);
    }

    @Test
    void updateGenreTest() {
        Long genreId = 1L;
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName("Ненаучная фантастика");

        Genre genreToUpdate = new Genre();
        genreToUpdate.setId(genreId);
        genreToUpdate.setName("Научная фантастика");

        when(genreRepo.findAll()).thenReturn(List.of(genreToUpdate));
        when(genreRepo.findById(genreId)).thenReturn(Optional.of(genreToUpdate));
        when(genreRepo.save(any(Genre.class))).thenReturn(genre);

        GenreDTO updatedGenre = new GenreDTO();
        updatedGenre.setId(genreId);
        updatedGenre.setName("Ненаучная фантастика");

        when(genreMapper.convert(any(Genre.class))).thenReturn(updatedGenre);

        GenreDTO actual = genreService.update(genre);

        assertEquals(updatedGenre, actual);

        verify(genreRepo).findById(genreId);
        verify(genreRepo).findAll();
        verify(genreRepo).save(any(Genre.class));
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    void deleteGenreWhenGenreIsNullTest() {
        Long genreId = 1L;
        when(genreRepo.findById(genreId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            genreService.delete(genreId);
        });

        assertEquals("Жанр с id " + genreId + " не найден.", exception.getMessage());
        verify(genreRepo).findById(genreId);
        verifyNoMoreInteractions(genreRepo);
    }

    @Test
    void deleteGenreTest() {
        Long genreId = 1L;
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName("Научная фантастика");

        when(genreRepo.findById(genreId)).thenReturn(Optional.of(genre));

        genreService.delete(genreId);

        verify(genreRepo).findById(genreId);
        verify(genreRepo).deleteById(genreId);
        verifyNoMoreInteractions(genreRepo);
    }
}
