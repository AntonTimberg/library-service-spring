package org.example.service;

import org.example.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Optional<Author> findById(int id);
    List<Author> findAll();
    void save(Author author);
    void update(Author author);
    void delete(int id);
}
