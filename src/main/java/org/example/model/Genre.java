package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Genre {
    private int id;
    private String name;
    private List<Book> books = new ArrayList<>();

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        if (!this.books.contains(book)) {
            this.books.add(book);
            book.getGenres().add(this);
        }
    }

    public void removeBook(Book book) {
        if (this.books.remove(book)) {
            book.getGenres().remove(this);
        }
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", books=" + books +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return id == genre.id && Objects.equals(name, genre.name) && Objects.equals(books, genre.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, books);
    }
}
