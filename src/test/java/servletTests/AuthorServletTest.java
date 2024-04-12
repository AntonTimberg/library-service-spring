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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AuthorServletTest {
    private AuthorService authorService;
    private Converter<Author, AuthorDTO> toDtoConverter;
    private Converter<AuthorDTO, Author> toEntityConverter;
    private AuthorServlet authorServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter printWriter;
    private StringWriter responseWriter;

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
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doGetWithValidId() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        Author author = new Author("Test Author");
        author.setId(1);
        AuthorDTO authorDto = new AuthorDTO();
        authorDto.setName("Test Author");

        when(authorService.findById(1)).thenReturn(Optional.of(author));
        when(toDtoConverter.convert(author)).thenReturn(authorDto);

        authorServlet.doGet(request, response);

        String capturedOutput = responseWriter.toString();

        assertTrue(capturedOutput.contains("Test Author"));
        verify(authorService).findById(1);
        verify(toDtoConverter).convert(author);
    }

    @Test
    void doGetWithInvalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");

        authorServlet.doGet(request, response);

        String capturedOutput = responseWriter.toString();
        assertTrue(capturedOutput.contains("Invalid author ID format"));
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
    void doPostWithoutName() throws Exception {
        String jsonInput = "{\"someOtherField\":\"Some Value\"}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        authorServlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Author name is required");
    }

    @Test
    void doPutWithValidDataUpdatesAuthor() throws Exception {
        String jsonInput = "{\"id\":1, \"name\":\"Updated Author\"}";
        AuthorDTO authorDto = new AuthorDTO();
        authorDto.setId(1);
        authorDto.setName("Updated Author");
        Author author = new Author("Updated Author");
        author.setId(1);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(toEntityConverter.convert(any(AuthorDTO.class))).thenReturn(author);

        authorServlet.doPut(request, response);

        verify(authorService).update(author);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verifyNoMoreInteractions(authorService, response);
    }

    @Test
    void doPutWithInvalidId() throws Exception {
        String jsonInput = "{\"id\":0, \"name\":\"Author\"}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        authorServlet.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid author ID is required");
        verifyNoMoreInteractions(authorService);
    }

    @Test
    void doPutWithEmptyName() throws Exception {
        String jsonInput = "{\"id\":1, \"name\":\"\"}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));

        authorServlet.doPut(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Author name is required");
        verifyNoMoreInteractions(authorService);
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
