package org.example.dao;

import org.example.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDAO {
    Optional<Author> findById(int id);
    List<Author> findAll();
    void save(Author author);
    void update(Author author);
    void delete(int id);
}
