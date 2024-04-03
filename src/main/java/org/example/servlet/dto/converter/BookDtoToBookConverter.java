package org.example.servlet.dto.converter;

import org.example.model.Book;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.example.servlet.dto.BookDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookDtoToBookConverter implements Converter<BookDTO, Book> {
    private final GenreService genreService;

    public BookDtoToBookConverter(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    public Book convert(BookDTO source) {
        Book book = new Book();
        book.setId(source.getId());
        book.setTitle(source.getTitle());
        book.setAuthorId(source.getAuthorId());

        if (source.getGenreIds() != null && !source.getGenreIds().isEmpty()) {
            List<Genre> genres = source.getGenreIds().stream()
                    .map(genreId -> genreService.findById(genreId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            book.setGenres(genres);
        }

        return book;
    }
}

