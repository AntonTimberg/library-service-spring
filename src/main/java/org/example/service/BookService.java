package org.example.service;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(int id);
    List<Book> findAll();
    void save(Book book);
    void update(Book book);
    void delete(int id);
}
