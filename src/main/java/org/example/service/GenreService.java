package org.example.service;

import org.example.model.Genre;
import org.example.controller.dto.GenreDTO;

import java.util.List;
import java.util.Set;

public interface GenreService {
    GenreDTO findById(Long id);
    List<GenreDTO> findAll();
    Set<Genre> findAllByIds(List<Long> ids);
    GenreDTO save(Genre genre);
    GenreDTO update(Genre genre);
    void delete(Long id);
}
