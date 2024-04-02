package org.example.servlet.dto;

import org.example.model.Author;
import java.util.stream.Collectors;

public class AuthorToAuthorDtoConverter implements Converter<Author, AuthorDTO> {
    @Override
    public AuthorDTO convert(Author source) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setBookIds(source.getBooks().stream().map(book -> book.getId()).collect(Collectors.toList()));
        return dto;
    }
}
