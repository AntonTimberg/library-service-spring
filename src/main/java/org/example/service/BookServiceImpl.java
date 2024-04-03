package org.example.service;

import org.example.dao.BookDAO;
import org.example.dao.BookDAOImpl;
import org.example.model.Book;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService{
    private final BookDAO bookDAO;

    public BookServiceImpl() {
        this.bookDAO = new BookDAOImpl();
    }

    public BookServiceImpl(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public Optional<Book> findById(int id) {
        return bookDAO.findById(id);
    }

    @Override
    public List<Book> findAll() {
        return bookDAO.findAll();
    }

    @Override
    public void save(Book book) {
        validateBook(book);
        bookDAO.save(book);
    }

    @Override
    public void update(Book book) {
        validateBook(book);
        bookDAO.update(book);
    }

    @Override
    public void delete(int id) {
        bookDAO.delete(id);
    }

    private void validateBook(Book book){
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название книги не может быть пустым");
        }

        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            throw new IllegalArgumentException("Книга должна быть связана c хотя бы одним жанром.");
        }
    }
}
