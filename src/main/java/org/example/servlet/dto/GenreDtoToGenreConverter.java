package org.example.servlet.dto;

import org.example.model.Genre;

public class GenreDtoToGenreConverter implements Converter<GenreDTO, Genre> {
    @Override
    public Genre convert(GenreDTO source) {
        Genre genre = new Genre();
        genre.setId(source.getId());
        genre.setName(source.getName());
        return genre;
    }
}
