package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.controller.dto.BookDTO;
import org.example.controller.dto.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepo;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepo;

    @Autowired
    public BookServiceImpl(BookRepository bookRepo, AuthorRepository authorRepo, BookMapper bookMapper) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.bookMapper = bookMapper;
    }

    @Override
    @Transactional
    public BookDTO findById(Long id) {
        return bookRepo.findById(id)
                .map(bookMapper::convert)
                .orElseThrow(() -> new EntityNotFoundException("Книга с id " + id + " не найдена"));
    }

    @Override
    @Transactional
    public List<BookDTO> findAll() {
        List<Book> books = bookRepo.findAll();
        return books.stream().map(bookMapper::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookDTO save(Book book) {
        validateBook(book);

        Author author = authorRepo.findById(book.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Автор книги с id=" + book.getAuthor().getId() + " не найден."));
//        book.setAuthor(author);
//        author.addBook(book);

        return bookMapper.convert(bookRepo.save(book));
    }

    @Override
    @Transactional
    public BookDTO update(Book book) {
        validateBook(book);

        Author author = authorRepo.findById(book.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Автор книги с id=" + book.getAuthor().getId() + " не найден."));

        Book existingBook = bookRepo.findById(book.getId())
              .orElseThrow(() -> new EntityNotFoundException("Книга с id=" + book.getId() + " не найдена."));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(author);
        existingBook.setGenres(book.getGenres());



        return bookMapper.convert(bookRepo.save(existingBook));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookRepo.findById(id).get();

        if (book != null) {
            bookRepo.deleteById(id);
        } else throw new EntityNotFoundException("Книга с id=" + book.getId() + " не найдена");
    }

    private void validateBook(Book book){
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название книги не может быть пустым");
        }
    }
}
