package org.example.service;

import org.example.model.Author;
import org.example.controller.dto.AuthorDTO;

import java.util.List;

public interface AuthorService {
    AuthorDTO findById(Long id);
    List<AuthorDTO> findAll();
    AuthorDTO save(Author author);
    AuthorDTO update(Author author);
    void delete(Long id);
}
