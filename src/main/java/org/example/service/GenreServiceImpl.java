package org.example.service;

import org.example.dao.GenreDAO;
import org.example.dao.GenreDAOImpl;
import org.example.model.Genre;

import java.util.List;
import java.util.Optional;

public class GenreServiceImpl implements GenreService{
    private final GenreDAO genreDAO = new GenreDAOImpl();

    @Override
    public Optional<Genre> findById(int id) {
        return genreDAO.findById(id);
    }

    @Override
    public List<Genre> findAll() {
        return genreDAO.findAll();
    }

    @Override
    public void save(Genre genre) {
        validateGenre(genre);
        genreDAO.save(genre);
    }

    @Override
    public void update(Genre genre) {
        validateGenre(genre);
        genreDAO.update(genre);
    }

    @Override
    public void delete(int id) {
        genreDAO.delete(id);
    }

    private void validateGenre(Genre genre) {
        if (genre.getName() == null || genre.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название жанра не может быть пустым");
        }
    }
}
