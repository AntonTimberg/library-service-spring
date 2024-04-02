package org.example.dao;

import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDAO {
    Optional<Book> findById(int id);
    List<Book> findAll();
    List<Book> findByAuthorId(int authorId);
    Book save(Book book);
    void update(Book book);
    void delete(int id);
}
