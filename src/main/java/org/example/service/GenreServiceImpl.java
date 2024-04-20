package org.example.service;

import jakarta.inject.Named;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.model.Genre;
import org.example.repository.GenreRepository;
import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService{
    private final GenreMapper genreMapper;
    private final GenreRepository genreRepo;

    @Autowired
    public GenreServiceImpl(GenreMapper genreMapper, GenreRepository genreRepo) {
        this.genreMapper = genreMapper;
        this.genreRepo = genreRepo;
    }

    @Override
    public GenreDTO findById(Long id) {
        return genreRepo.findById(id)
                .map(genreMapper::convert)
                .orElseThrow(() -> new EntityNotFoundException("Жанр с id " + id + " не найден."));
    }

    @Override
    public List<GenreDTO> findAll() {
        List<Genre> genres = genreRepo.findAll();
        return genres.stream().map(genreMapper::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreDTO save(Genre genre) {
        validateGenre(genre);

        boolean genreExists = genreRepo.findAll().stream()
                .anyMatch(existingGenre -> existingGenre.getName().equalsIgnoreCase(genre.getName()));
        if (genreExists) {
            throw new IllegalArgumentException("Жанр с именем " + genre.getName() + " уже существует.");
        }

        return genreMapper.convert(genreRepo.save(genre));
    }

    @Override
    @Transactional
    public GenreDTO update(Genre genre) {
        validateGenre(genre);

        boolean genreExists = genreRepo.findAll().stream()
                .anyMatch(existingGenre -> existingGenre.getName().equalsIgnoreCase(genre.getName()));
        if (genreExists) {
            throw new IllegalArgumentException("Жанр с именем " + genre.getName() + " уже существует.");
        }

        Genre existingGenre = genreRepo.findById(genre.getId()).get();
        if (existingGenre == null) {
            throw new EntityNotFoundException("Жанр с id=" + genre.getId() + " не найден");
        }
        existingGenre.setName(genre.getName());

        return genreMapper.convert(genreRepo.save(existingGenre));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Genre genre = genreRepo.findById(id).get();
        if (genre != null) {
            genreRepo.deleteById(id);
        } else throw new EntityNotFoundException("Жанр с id " + id + " не найден.");
    }

    @Override
    @Transactional
    public Set<Genre> findAllByIds(List<Long> ids) {
        return new HashSet<>(genreRepo.findAllById(ids));
    }

    private void validateGenre(Genre genre){
        if (genre.getName() == null || genre.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название жанра, не может быть пустым.");
        }
    }
}
