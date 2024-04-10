package servletTests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.example.servlet.GenreServlet;
import org.example.servlet.dto.GenreDTO;
import org.example.servlet.dto.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenreServletTest {
    private GenreService genreService;
    private Converter<Genre, GenreDTO> toDtoConverter;
    private Converter<GenreDTO, Genre> toEntityConverter;
    private GenreServlet genreServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        genreService = Mockito.mock(GenreService.class);
        toDtoConverter = Mockito.mock(Converter.class);
        toEntityConverter = Mockito.mock(Converter.class);
        genreServlet = new GenreServlet(genreService, toDtoConverter, toEntityConverter);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doGetWithoutIdReturnsAllGenres() throws Exception {
        List<Genre> genres = List.of(new Genre("Fiction"), new Genre("Pulp Fiction"));
        List<GenreDTO> genresDto = genres.stream()
                .map(g -> new GenreDTO(g.getId(), g.getName()))
                .collect(Collectors.toList());

        when(genreService.findAll()).thenReturn(genres);
        when(toDtoConverter.convert(any(Genre.class))).thenAnswer(i -> {
            Genre genre = i.getArgument(0);
            return new GenreDTO(genre.getId(), genre.getName());
        });

        genreServlet.doGet(request, response);

        verify(genreService).findAll();
        verify(response).setContentType("application/json");

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Fiction") && jsonResponse.contains("Pulp Fiction"));
    }

    @Test
    void doPostCreateGenre() throws Exception {
        String jsonInput = "{\"name\":\"Horror\"}";
        GenreDTO genreDtoInput = new GenreDTO();
        genreDtoInput.setName("Horror");

        Genre genre = new Genre("Horror");

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(toEntityConverter.convert(any(GenreDTO.class))).thenReturn(genre);

        doNothing().when(genreService).save(any(Genre.class));

        genreServlet.doPost(request, response);

        verify(genreService).save(any(Genre.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void doPutUpdatesGenre() throws Exception {
        String jsonInput = "{\"id\":1, \"name\":\"new Genre\"}";
        GenreDTO genreDto = new GenreDTO(1, "new Genre");

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(toEntityConverter.convert(any(GenreDTO.class))).thenReturn(new Genre("new Genre"));

        doNothing().when(genreService).update(any(Genre.class));

        genreServlet.doPut(request, response);

        verify(genreService).update(any(Genre.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteGenre() throws Exception {
        String pathInfo = "/1";
        when(request.getPathInfo()).thenReturn(pathInfo);

        doNothing().when(genreService).delete(1);

        genreServlet.doDelete(request, response);

        verify(genreService).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
