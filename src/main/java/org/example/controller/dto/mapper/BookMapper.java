package org.example.controller.dto.mapper;

import org.mapstruct.Named;
import org.example.model.Book;
import org.example.controller.dto.BookDTO;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "genres", target = "genreIds", qualifiedByName = "genresToIds")
    BookDTO convert(Book book);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "genreIds", target = "genres", qualifiedByName = "idsToGenres")
    Book convert(BookDTO bookDTO, @Context GenreService genreService);

    @Named("genresToIds")
    default List<Long> genresToIds(Set<Genre> genres) {
        return genres != null ? genres.stream().map(Genre::getId).collect(Collectors.toList()) : null;
    }

    @Named("idsToGenres")
    default Set<Genre> idsToGenres(List<Long> ids, @Context GenreService genreService) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(genreService.findAllByIds(ids));
    }
}
