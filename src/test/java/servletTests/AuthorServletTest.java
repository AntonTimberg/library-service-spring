package servletTests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Author;
import org.example.service.AuthorService;
import org.example.servlet.AuthorServlet;
import org.example.servlet.dto.AuthorDTO;
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorServletTest {
    private AuthorService authorService;
    private Converter<Author, AuthorDTO> toDtoConverter;
    private Converter<AuthorDTO, Author> toEntityConverter;
    private AuthorServlet authorServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        authorService = Mockito.mock(AuthorService.class);
        toDtoConverter = Mockito.mock(Converter.class);
        toEntityConverter = Mockito.mock(Converter.class);
        authorServlet = new AuthorServlet(authorService, toDtoConverter, toEntityConverter);

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);

        Mockito.when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doPostWithValidAuthor() throws Exception {
        String jsonInput = "{\"name\":\"Тестовый Автор\"}";
        Author author = new Author("Тестовый Автор");
        AuthorDTO authorDto = new AuthorDTO();
        authorDto.setName("Тестовый Автор");

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(toEntityConverter.convert(any(AuthorDTO.class))).thenReturn(author);
        when(toDtoConverter.convert(any(Author.class))).thenReturn(authorDto);

        authorServlet.doPost(request, response);

        verify(authorService).save(author);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        String jsonResponse = responseWriter.toString();
        assertThat(jsonResponse, containsString("\"name\":\"Тестовый Автор\""));
    }

    @Test
    void doGetWithoutIdReturnsAllAuthors() throws Exception {
        List<Author> authors = List.of(new Author("Автор 1"), new Author("Автор 2"));

        when(request.getPathInfo()).thenReturn("/");
        when(authorService.findAll()).thenReturn(authors);
        when(toDtoConverter.convert(any(Author.class))).thenAnswer(i -> {
            Author author = i.getArgument(0);
            AuthorDTO dto = new AuthorDTO();
            dto.setName(author.getName());
            return dto;
        });

        authorServlet.doGet(request, response);

        verify(authorService).findAll();

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Автор 1") && jsonResponse.contains("Автор 2"));
    }

    @Test
    void doDeleteAuthor() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doNothing().when(authorService).delete(1);

        authorServlet.doDelete(request, response);

        verify(authorService).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
