package org.example.controller.dto.mapper;

import org.example.model.Author;
import org.example.controller.dto.AuthorDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDTO convert(Author author);
    Author convert(AuthorDTO authorDTO);
}
