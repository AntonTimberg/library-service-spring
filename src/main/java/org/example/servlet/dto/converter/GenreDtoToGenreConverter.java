package org.example.servlet.dto.converter;

import org.example.model.Genre;
import org.example.servlet.dto.GenreDTO;

public class GenreDtoToGenreConverter implements Converter<GenreDTO, Genre> {
    @Override
    public Genre convert(GenreDTO source) {
        Genre genre = new Genre();
        genre.setId(source.getId());
        genre.setName(source.getName());
        return genre;
    }
}
