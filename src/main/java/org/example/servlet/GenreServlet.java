package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.example.service.GenreServiceImpl;
import org.example.servlet.dto.GenreDTO;
import org.example.servlet.dto.converter.Converter;
import org.example.servlet.dto.converter.GenreDtoToGenreConverter;
import org.example.servlet.dto.converter.GenreToGenreDtoConverter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/genre/*")
public class GenreServlet extends HttpServlet {

    private GenreService genreService;
    private Converter<Genre, GenreDTO> toDtoConverter;
    private Converter<GenreDTO, Genre> toEntityConverter;

    public GenreServlet() {
    }

    public GenreServlet(GenreService genreService, Converter<Genre, GenreDTO> toDtoConverter, Converter<GenreDTO, Genre> toEntityConverter) {
        this.genreService = genreService;
        this.toDtoConverter = toDtoConverter;
        this.toEntityConverter = toEntityConverter;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.genreService = new GenreServiceImpl();
        this.toDtoConverter = new GenreToGenreDtoConverter();
        this.toEntityConverter = new GenreDtoToGenreConverter();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Genre genre = genreService.findById(id).orElse(null);
                if (genre != null) {
                    GenreDTO genreDto = toDtoConverter.convert(genre);
                    response.getWriter().write(gson.toJson(genreDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Genre not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid genre ID format\"}");
            }
        } else {
            List<GenreDTO> genresDto = genreService.findAll().stream()
                    .map(toDtoConverter::convert)
                    .collect(Collectors.toList());
            response.getWriter().write(gson.toJson(genresDto));
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            GenreDTO genreDto = gson.fromJson(request.getReader(), GenreDTO.class);
            Genre genre = toEntityConverter.convert(genreDto);
            if (genre == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid data\"}");
                return;
            }
            genreService.save(genre);
            genreDto.setId(genre.getId());
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(genreDto));
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
            GenreDTO genreDto = gson.fromJson(request.getReader(), GenreDTO.class);
            Genre genre = toEntityConverter.convert(genreDto);
            genreService.update(genre);
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Genre ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            genreService.delete(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid genre ID format");
        }
    }
}
