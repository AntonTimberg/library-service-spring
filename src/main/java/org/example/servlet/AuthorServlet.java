package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Author;
import org.example.service.AuthorService;
import org.example.service.AuthorServiceImpl;
import org.example.servlet.dto.AuthorDTO;
import org.example.servlet.dto.converter.AuthorDtoToAuthorConverter;
import org.example.servlet.dto.converter.AuthorToAuthorDtoConverter;
import org.example.servlet.dto.converter.Converter;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/author/*")
public class AuthorServlet extends HttpServlet {
    private AuthorService authorService;
    private Converter<Author, AuthorDTO> toDtoConverter;
    private Converter<AuthorDTO, Author> toEntityConverter;

    public AuthorServlet() {
    }

    public AuthorServlet(AuthorService authorService, Converter<Author, AuthorDTO> toDtoConverter, Converter<AuthorDTO, Author> toEntityConverter) {
        this.authorService = authorService;
        this.toDtoConverter = toDtoConverter;
        this.toEntityConverter = toEntityConverter;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.authorService = new AuthorServiceImpl();
        this.toDtoConverter = new AuthorToAuthorDtoConverter();
        this.toEntityConverter = new AuthorDtoToAuthorConverter();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");

        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<Author> author = authorService.findById(id);
                if (author.isPresent()) {
                    AuthorDTO authorDto = toDtoConverter.convert(author.get());
                    String json = new Gson().toJson(authorDto);
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Author not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid author ID format\"}");
            }
        } else {
            List<AuthorDTO> authorsDto = authorService.findAll().stream()
                    .map(toDtoConverter::convert)
                    .collect(Collectors.toList());
            String json = new Gson().toJson(authorsDto);
            response.getWriter().write(json);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        AuthorDTO authorDto;
        try {
            authorDto = gson.fromJson(request.getReader(), AuthorDTO.class);
            if (authorDto.getName() == null || authorDto.getName().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author name is required");
                return;
            }
            Author author = toEntityConverter.convert(authorDto);
            authorService.save(author);
            authorDto.setId(author.getId());
            String json = gson.toJson(authorDto);
            response.setContentType("application/json");
            response.getWriter().write(json);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JsonSyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        try {
            AuthorDTO authorDto = gson.fromJson(request.getReader(), AuthorDTO.class);
            if (authorDto.getId() <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid author ID is required");
                return;
            }
            if (authorDto.getName() == null || authorDto.getName().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author name is required");
                return;
            }
            Author author = toEntityConverter.convert(authorDto);
            authorService.update(author);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (JsonSyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Author ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            authorService.delete(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid author ID format");
        }
    }
}
