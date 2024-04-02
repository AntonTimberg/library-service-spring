package org.example.servlet.dto.converter;

import org.example.model.Book;
import java.util.stream.Collectors;
import org.example.model.Genre;
import org.example.servlet.dto.BookDTO;


public class BookToBookDtoConverter implements Converter<Book, BookDTO> {
    @Override
    public BookDTO convert(Book source) {
        BookDTO dto = new BookDTO();
        dto.setId(source.getId());
        dto.setTitle(source.getTitle());
        dto.setAuthorId(source.getAuthorId());
        dto.setGenreIds(source.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        return dto;
    }
}
