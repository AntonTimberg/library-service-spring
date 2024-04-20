package org.example.controller;

import org.example.controller.dto.BookDTO;
import org.example.controller.dto.mapper.BookMapper;
import org.example.service.BookService;
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
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private GenreService genreService;

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable("id") Long id) {
        BookDTO book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.save(bookMapper.convert(bookDTO, genreService));
        return ResponseEntity.ok(createdBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long id, @RequestBody BookDTO bookDTO) {
        if (id == null) {
            throw new IllegalArgumentException("ID книги не должен быть пустым");
        }

        bookDTO.setId(id);
        BookDTO updatedBook = bookService.update(bookMapper.convert(bookDTO, genreService));
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        bookService.delete(id);
        return ResponseEntity.ok().build();
    }
}
