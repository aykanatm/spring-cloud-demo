package com.github.murataykanat.springclouddemo.repository;

import com.github.murataykanat.springclouddemo.models.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookRepository {
    private static List<Book> books;
    private static BookRepository bookRepository;

    private BookRepository (List<Book> books){
        BookRepository.books = books;
    }

    public static BookRepository getInstance(){
        if(bookRepository != null){
            return bookRepository;
        }

        books = new ArrayList<>();

        Book book1 = new Book("1","book1", "author1");
        Book book2 = new Book("2","book2", "author2");
        Book book3 = new Book("3","book3", "author3");

        books.add(book1);
        books.add(book2);
        books.add(book3);

        bookRepository = new BookRepository(books);
        return bookRepository;
    }

    public Book getBook(String bookID) throws Exception {
        List<Book> collect = books.stream().filter(book -> book.getId().equalsIgnoreCase(bookID)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            if(collect.size() == 1){
                return collect.get(0);
            }
            else{
                throw new Exception("Multiple books with the same name found!");
            }
        }

        return null;
    }
}
