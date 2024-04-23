package controllerTest;

import org.example.controller.AuthorController;
import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.example.model.Author;
import org.example.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthorService authorService;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorController authorController;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(authorController).build();
    }

    @Test
    void getAuthorTest() throws Exception {
        Long id = 1L;
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(1);
        authorDTO.setName("А.С. Пушкин");

        when(authorService.findById(id)).thenReturn(authorDTO);

        mockMvc.perform(get("/authors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("А.С. Пушкин"));

        verify(authorService).findById(id);
    }

    @Test
    void getAllAuthorsTest() throws Exception {
        var author1 = new AuthorDTO();
        author1.setId(1);
        author1.setName("Пушкин");
        var author2 = new AuthorDTO();
        author2.setId(1);
        author2.setName("Есенин");

        List<AuthorDTO> authors = Arrays.asList(
                author1, author2
        );

        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Пушкин"))
                .andExpect(jsonPath("$[1].name").value("Есенин"));

        verify(authorService).findAll();
    }

    @Test
    void createAuthorTest() throws Exception {
        var authorDTO = new AuthorDTO();
        authorDTO.setName("Пушкин");
        var author = new Author();
        authorDTO.setName("Пушкин");

        when(authorMapper.convert(any(AuthorDTO.class))).thenReturn(author);
        when(authorService.save(any(Author.class))).thenReturn(authorDTO);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Пушкин\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Пушкин"));

        verify(authorService).save(any());
    }

    @Test
    void updateAuthorTest() throws Exception {
        int id = 1;
        var authorDTO = new AuthorDTO();
        authorDTO.setId(id);
        authorDTO.setName("Пушкин");
        var author = new Author();
        authorDTO.setId(id);
        authorDTO.setName("Пушкин");

        when(authorMapper.convert(any(AuthorDTO.class))).thenReturn(author);
        when(authorService.update(any(Author.class))).thenReturn(authorDTO);


        mockMvc.perform(put("/authors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Пушкин\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Пушкин"));

        verify(authorService).update(any(Author.class));
    }

    @Test
    void deleteAuthorTest() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/authors/{id}", id))
                .andExpect(status().isOk());

        verify(authorService).delete(id);
    }
}
