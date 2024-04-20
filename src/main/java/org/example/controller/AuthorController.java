package org.example.controller;

import org.example.controller.dto.AuthorDTO;
import org.example.controller.dto.mapper.AuthorMapper;
import org.example.service.AuthorService;
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
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private AuthorMapper authorMapper;

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable("id") Long id) {
        AuthorDTO author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<AuthorDTO> authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO) {
        AuthorDTO createdAuthor = authorService.save(authorMapper.convert(authorDTO));
        return ResponseEntity.ok(createdAuthor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable("id") int id, @RequestBody AuthorDTO authorDTO) {
        authorDTO.setId(id);
        AuthorDTO updatedAuthor = authorService.update(authorMapper.convert(authorDTO));
        return ResponseEntity.ok(updatedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id) {
        authorService.delete(id);
        return ResponseEntity.ok().build();
    }
}
