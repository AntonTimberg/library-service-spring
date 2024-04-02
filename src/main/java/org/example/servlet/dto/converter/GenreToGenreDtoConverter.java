package org.example.servlet.dto.converter;

import org.example.model.Genre;
import org.example.servlet.dto.GenreDTO;

public class GenreToGenreDtoConverter implements Converter<Genre, GenreDTO> {
    @Override
    public GenreDTO convert(Genre source) {
        GenreDTO dto = new GenreDTO();
        dto.setId(source.getId());
        dto.setName(source.getName());
        return dto;
    }
}
