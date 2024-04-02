package org.example.servlet.dto;

import org.example.model.Author;

public class AuthorDtoToAuthorConverter implements Converter<AuthorDTO, Author>{
    @Override
    public Author convert(AuthorDTO source) {
        Author author = new Author();
        author.setId(source.getId());
        author.setName(source.getName());
        return author;
    }
}
