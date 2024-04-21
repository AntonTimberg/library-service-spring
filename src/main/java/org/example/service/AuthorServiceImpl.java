package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepo;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepo, AuthorMapper authorMapper) {
        this.authorRepo = authorRepo;
        this.authorMapper = authorMapper;
    }

    @Override
    public AuthorDTO findById(Long id) {
        return authorRepo.findById(id)
                .map(authorMapper::convert)
                .orElseThrow(() -> new EntityNotFoundException("Автор с id=" + id + " не найден"));
    }

    @Override
    public List<AuthorDTO> findAll() {
        List<Author> authors = authorRepo.findAll();
        return authors.stream().map(authorMapper::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuthorDTO save(Author author) {
        validateAuthor(author);
        return authorMapper.convert(authorRepo.save(author));
    }

    @Override
    @Transactional
    public AuthorDTO update(Author author) {
        validateAuthor(author);

        if (!authorRepo.existsById(author.getId())) {
            throw new EntityNotFoundException("Автор с id=" + author.getId() + " не найден");
        }

        return authorMapper.convert(authorRepo.save(author));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Author author = authorRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Автор с id=" + id + " не найден"));
        authorRepo.deleteById(id);
    }

    private void validateAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя автора не может быть пустым");
        }
    }
}
