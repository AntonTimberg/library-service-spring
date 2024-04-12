package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Book;
import org.example.service.BookService;
import org.example.service.BookServiceImpl;
import org.example.service.GenreService;
import org.example.service.GenreServiceImpl;
import org.example.servlet.dto.BookDTO;
import org.example.servlet.dto.converter.BookDtoToBookConverter;
import org.example.servlet.dto.converter.BookToBookDtoConverter;
import org.example.servlet.dto.converter.Converter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/book/*")
public class BookServlet extends HttpServlet {

    private BookService bookService;
    private Converter<Book, BookDTO> toDtoConverter;
    private Converter<BookDTO, Book> toEntityConverter;

    public BookServlet() {
    }

    public BookServlet(BookService bookService, Converter<Book, BookDTO> toDtoConverter, Converter<BookDTO, Book> toEntityConverter) {
        this.bookService = bookService;
        this.toDtoConverter = toDtoConverter;
        this.toEntityConverter = toEntityConverter;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.bookService = new BookServiceImpl();
        GenreService genreService = new GenreServiceImpl();
        this.toDtoConverter = new BookToBookDtoConverter();
        this.toEntityConverter = new BookDtoToBookConverter(genreService);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        Gson gson = new Gson();

        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                Book book = bookService.findById(id).orElse(null);
                if (book != null) {
                    BookDTO bookDto = toDtoConverter.convert(book);
                    String json = gson.toJson(bookDto);
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Book not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid book ID format\"}");
            }
        } else {
            List<BookDTO> booksDto = bookService.findAll().stream()
                    .map(toDtoConverter::convert)
                    .collect(Collectors.toList());
            String json = gson.toJson(booksDto);
            response.getWriter().write(json);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            BookDTO bookDto = gson.fromJson(request.getReader(), BookDTO.class);
            Book book = toEntityConverter.convert(bookDto);

            if (book == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid book data\"}");
                return;
            }

            bookService.save(book);
            bookDto.setId(book.getId());

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(bookDto));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid JSON data\"}");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            BookDTO bookDto = gson.fromJson(request.getReader(), BookDTO.class);
            Book book = toEntityConverter.convert(bookDto);
            bookService.update(book);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Invalid JSON data\"}");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Book ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            bookService.delete(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID format");
        }
    }
}
