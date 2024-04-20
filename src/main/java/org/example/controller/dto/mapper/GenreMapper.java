package org.example.controller.dto.mapper;

import org.example.model.Genre;
import org.example.controller.dto.GenreDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDTO convert(Genre genre);
    Genre convert(GenreDTO genreDTO);
}
