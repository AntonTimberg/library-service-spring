package org.example.service;

import org.example.model.Book;
import org.example.controller.dto.BookDTO;

import java.util.List;

public interface BookService {
    BookDTO findById(Long id);
    List<BookDTO> findAll();
    BookDTO save(Book book);
    BookDTO update(Book book);
    void delete(Long id);
}
