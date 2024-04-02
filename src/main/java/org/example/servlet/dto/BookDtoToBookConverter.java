package org.example.servlet.dto;

import org.example.model.Book;

public class BookDtoToBookConverter implements Converter<BookDTO, Book> {
    @Override
    public Book convert(BookDTO source) {
        Book book = new Book();
        book.setId(source.getId());
        book.setTitle(source.getTitle());
        book.setAuthorId(source.getAuthorId());
        return book;
    }
}
