package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.BookController;
import org.example.controller.dto.BookDTO;
import org.example.controller.dto.mapper.BookMapper;
import org.example.model.Book;
import org.example.service.BookService;
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
class BookControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(bookController).build();
    }

    @Test
    void getBookTest() throws Exception {
        Long id = 1L;
        BookDTO bookDTO = new BookDTO(id, "Война и мир", 1L, List.of(1L, 2L));

        when(bookService.findById(id)).thenReturn(bookDTO);

        mockMvc.perform(get("/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Война и мир"));

        verify(bookService).findById(id);
    }

    @Test
    void getAllBooksTest() throws Exception {
        BookDTO book1 = new BookDTO(1L, "Война и мир", 1L, List.of(1L));
        BookDTO book2 = new BookDTO(2L, "1984", 2L, List.of(2L));

        when(bookService.findAll()).thenReturn(List.of(book1, book2));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Война и мир"))
                .andExpect(jsonPath("$[1].title").value("1984"));

        verify(bookService).findAll();
    }

    @Test
    void createBookTest() throws Exception {
        var bookID = 1L;
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(bookID);
        bookDTO.setTitle("MM");
        Book book = new Book();
        book.setId(bookID);
        book.setTitle("MM");

        when(bookMapper.convert(any(BookDTO.class), any(GenreService.class))).thenReturn(book);
        when(bookService.save(any(Book.class))).thenReturn(bookDTO);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("MM"));

        verify(bookService).save(book);
    }

    @Test
    void updateBookTest() throws Exception {
        Long id = 1L;
        BookDTO bookDTO = new BookDTO(id, "Война и мир", id, List.of(id));

        when(bookMapper.convert(any(BookDTO.class), any(GenreService.class))).thenReturn(new Book());
        when(bookService.update(any(Book.class))).thenReturn(bookDTO);

        mockMvc.perform(put("/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Война и мир"));

        verify(bookService).update(any(Book.class));
    }

    @Test
    void deleteBookTest() throws Exception{
        Long id = 1L;

        mockMvc.perform(delete("/books/{id}", id))
                .andExpect(status().isOk());

        verify(bookService).delete(id);
    }
}
