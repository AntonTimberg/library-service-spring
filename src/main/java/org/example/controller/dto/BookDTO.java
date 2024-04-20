package org.example.controller.dto;

import java.util.List;

public class BookDTO {
    private Long id;
    private String title;
    private Long authorId;
    private List<Long> genreIds;

    public BookDTO() {}

    public BookDTO(Long id, String title, Long authorId, List<Long> genreIds) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreIds = genreIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", genreIds=" + genreIds +
                '}';
    }
}
