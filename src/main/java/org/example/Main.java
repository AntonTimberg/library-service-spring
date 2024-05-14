package org.example;

import org.example.controller.dto.mapper.BookMapper;
import org.example.service.AuthorService;
import org.example.service.BookService;
import org.example.service.GenreService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello!");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            AuthorService authorService = context.getBean(AuthorService.class);
            BookService bookService = context.getBean(BookService.class);
            GenreService genreService = context.getBean(GenreService.class);
            BookMapper mapper = context.getBean(BookMapper.class);
            System.out.println(mapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}