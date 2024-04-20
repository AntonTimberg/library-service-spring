package org.example;

import org.example.service.AuthorService;
import org.example.service.BookService;
import org.example.service.GenreService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml")) {
            AuthorService authorService = context.getBean(AuthorService.class);
            BookService bookService = context.getBean(BookService.class);
            GenreService genreService = context.getBean(GenreService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}