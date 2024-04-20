package org.example.controller;

import org.example.controller.dto.GenreDTO;
import org.example.controller.dto.mapper.GenreMapper;
import org.example.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;
    @Autowired
    private GenreMapper genreMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenre(@PathVariable("id") Long id) {
        GenreDTO genre = genreService.findById(id);
        return ResponseEntity.ok(genre);
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = genreService.findAll();
        return ResponseEntity.ok(genres);
    }

    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreDTO genreDTO) {
        GenreDTO createdGenre = genreService.save(genreMapper.convert(genreDTO));
        return ResponseEntity.ok(createdGenre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable("id") Long id, @RequestBody GenreDTO genreDTO) {
        genreDTO.setId(id);
        GenreDTO updatedGenre = genreService.update(genreMapper.convert(genreDTO));
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable("id") Long id) {
        genreService.delete(id);
        return ResponseEntity.ok().build();
    }
}
