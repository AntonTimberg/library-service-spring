package org.example.dao;

import org.example.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDAO {
    Optional<Genre> findById(int id);
    List<Genre> findAll();
    void save(Genre genre);
    void update(Genre genre);
    void delete(int id);
}
