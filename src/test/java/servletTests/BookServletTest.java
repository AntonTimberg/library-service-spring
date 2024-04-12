package servletTests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Book;
import org.example.service.BookService;
import org.example.servlet.BookServlet;
import org.example.servlet.dto.BookDTO;
import org.example.servlet.dto.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServletTest {
    private BookService bookService;
    private Converter<Book, BookDTO> toDtoConverter;
    private Converter<BookDTO, Book> toEntityConverter;
    private BookServlet bookServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        bookService = mock(BookService.class);
        toDtoConverter = mock(Converter.class);
        toEntityConverter = mock(Converter.class);
        bookServlet = new BookServlet(bookService, toDtoConverter, toEntityConverter);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void doGetWithoutIdReturnsAllBooks() throws Exception {
        List<Book> books = List.of(new Book("Book 1", 1), new Book("Book 2", 2));
        List<BookDTO> booksDto = books.stream()
                .map(b -> {
                    BookDTO dto = new BookDTO();
                    dto.setTitle(b.getTitle());
                    dto.setAuthorId(b.getAuthorId());
                    return dto;
                })
                .collect(Collectors.toList());

        when(request.getPathInfo()).thenReturn("/");
        when(bookService.findAll()).thenReturn(books);
        when(toDtoConverter.convert(any(Book.class))).thenAnswer(i -> {
            Book book = i.getArgument(0);
            BookDTO dto = new BookDTO();
            dto.setTitle(book.getTitle());
            dto.setAuthorId(book.getAuthorId());
            return dto;
        });

        bookServlet.doGet(request, response);

        verify(bookService).findAll();
        verify(response).setContentType("application/json");

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Book 1") && jsonResponse.contains("Book 2"));
    }

    @Test
    void doGetWithValidId() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        Book book = new Book("Book 1", 1);
        BookDTO bookDto = new BookDTO();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthorId(book.getAuthorId());

        when(bookService.findById(1)).thenReturn(Optional.of(book));
        when(toDtoConverter.convert(book)).thenReturn(bookDto);

        bookServlet.doGet(request, response);

        verify(bookService).findById(1);
        verify(response).setContentType("application/json");

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Book 1"));
        assertTrue(jsonResponse.contains("\"authorId\":1"));
    }

    @Test
    void doGetWithInvalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");

        bookServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid book ID format"));
    }

    @Test
    void doGetWithNonExistentId() throws Exception {
        Integer nonExistentId = 1888;

        when(request.getPathInfo()).thenReturn("/" + nonExistentId);
        when(bookService.findById(nonExistentId)).thenReturn(Optional.empty());

        bookServlet.doGet(request, response);

        verify(bookService).findById(nonExistentId);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Book not found"));
    }

    @Test
    void doPost() throws Exception {
        String jsonInput = "{\"title\":\"New Book\", \"authorId\":1}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonInput));
        when(request.getReader()).thenReturn(reader);

        Book expectedBook = new Book("New Book", 1);
        when(toEntityConverter.convert(any(BookDTO.class))).thenReturn(expectedBook);

        bookServlet.doPost(request, response);

        var bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertEquals(expectedBook.getTitle(), savedBook.getTitle());
        assertEquals(expectedBook.getAuthorId(), savedBook.getAuthorId());

        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("\"title\":\"New Book\""));
        assertTrue(jsonResponse.contains("\"authorId\":1"));
    }

    @Test
    void doPutUpdateBook() throws Exception {
        String jsonInput = "{\"id\":1, \"title\":\"Updated Book\", \"authorId\":1}";
        Book updatedBook = new Book("Updated Book", 1);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonInput)));
        when(toEntityConverter.convert(any(BookDTO.class))).thenReturn(updatedBook);

        doNothing().when(bookService).update(any(Book.class));

        bookServlet.doPut(request, response);

        verify(bookService).update(updatedBook);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteDeletesBook() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        doNothing().when(bookService).delete(1);

        bookServlet.doDelete(request, response);

        verify(bookService).delete(1);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
