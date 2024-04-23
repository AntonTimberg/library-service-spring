package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.GenreController;
import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GenreService genreService;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreController genreController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(genreController).build();
    }

    @Test
    void getGenreTest() throws Exception {
        Long id = 1L;
        GenreDTO genreDTO = new GenreDTO(id, "Роман");

        when(genreService.findById(id)).thenReturn(genreDTO);

        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Роман"));

        verify(genreService).findById(id);
    }

    @Test
    void getAllGenresTest() throws Exception {
        GenreDTO genre1 = new GenreDTO(1L, "Роман");
        GenreDTO genre2 = new GenreDTO(2L, "Научная фантастика");

        when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Роман"))
                .andExpect(jsonPath("$[1].name").value("Научная фантастика"));

        verify(genreService).findAll();
    }

    @Test
    void createGenreTest() throws Exception {
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setId(3L);
        genreDTO.setName("Ужасы");
        Genre genre = new Genre();
        genre.setId(3L);
        genre.setName("Ужасы");

        when(genreMapper.convert(any(GenreDTO.class))).thenReturn(genre);
        when(genreService.save(any(Genre.class))).thenReturn(genreDTO);

        mockMvc.perform(post("/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Ужасы"));

        verify(genreService).save(any(Genre.class));
    }

    @Test
    void updateGenreTest() throws Exception{
        Long id = 1L;
        GenreDTO genreDTO = new GenreDTO(id, "Ужасы");

        when(genreMapper.convert(any(GenreDTO.class))).thenReturn(new Genre("Ужасы"));
        when(genreService.update(any(Genre.class))).thenReturn(genreDTO);

        mockMvc.perform(put("/genres/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Ужасы"));

        verify(genreService).update(any(Genre.class));
    }

    @Test
    void deleteGenreTest() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/genres/{id}", id))
                .andExpect(status().isOk());

        verify(genreService).delete(id);
    }
}
