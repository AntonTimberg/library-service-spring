package org.example.servlet.dto.converter;

import org.example.model.Author;
import org.example.servlet.dto.AuthorDTO;

public class AuthorDtoToAuthorConverter implements Converter<AuthorDTO, Author>{
    @Override
    public Author convert(AuthorDTO source) {
        Author author = new Author();
        author.setId(source.getId());
        author.setName(source.getName());
        return author;
    }
}
