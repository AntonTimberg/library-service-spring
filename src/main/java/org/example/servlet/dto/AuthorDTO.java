package org.example.servlet.dto;

import java.util.List;

public class AuthorDTO {
    private int id;
    private String name;
    private List<Integer> bookIds;

    public AuthorDTO(int id, String name, List<Integer> bookIds) {
        this.id = id;
        this.name = name;
        this.bookIds = bookIds;
    }

    public AuthorDTO() {
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

    public List<Integer> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<Integer> bookIds) {
        this.bookIds = bookIds;
    }

    @Override
    public String toString() {
        return "AuthorDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bookIds=" + bookIds +
                '}';
    }
}
