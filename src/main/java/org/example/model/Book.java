package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Book {
    private int id;
    private String title;
    private int authorId;
    private List<Genre> genres = new ArrayList<>();

    public Book() {}

    public Book(String title, int authorId) {
        this.title = title;
        this.authorId = authorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void addGenre(Genre genre) {
        if (!this.genres.contains(genre)) {
            this.genres.add(genre);
            genre.getBooks().add(this);
        }
    }

    public void removeGenre(Genre genre) {
        if (this.genres.remove(genre)) {
            genre.getBooks().remove(this);
        }
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", genres=" + genres +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && authorId == book.authorId && Objects.equals(title, book.title) && Objects.equals(genres, book.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, authorId, genres);
    }
}
