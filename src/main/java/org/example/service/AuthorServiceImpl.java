package org.example.service;

import org.example.dao.AuthorDAO;
import org.example.dao.AuthorDAOImpl;
import org.example.model.Author;

import java.util.List;
import java.util.Optional;

public class AuthorServiceImpl implements AuthorService{
    private final AuthorDAO authorDAO;

    public AuthorServiceImpl() {
        this.authorDAO = new AuthorDAOImpl();
    }

    public AuthorServiceImpl(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    @Override
    public Optional<Author> findById(int id) {
        return authorDAO.findById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorDAO.findAll();
    }

    @Override
    public void save(Author author) {
        validateAuthor(author);
        authorDAO.save(author);
    }

    @Override
    public void update(Author author) {
        validateAuthor(author);
        authorDAO.update(author);
    }

    @Override
    public void delete(int id) {
        authorDAO.delete(id);
    }

    private void validateAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя автора не может быть пустым");
        }
    }
}
