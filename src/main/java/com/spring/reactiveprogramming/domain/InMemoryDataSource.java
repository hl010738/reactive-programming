package com.spring.reactiveprogramming.domain;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryDataSource {
    public static final Book[] books = new Book[]{
        new Book("0001", "CS book 1", BigDecimal.valueOf(19.99D), "CS"),
        new Book("0002", "CS book 2", BigDecimal.valueOf(9.99D), "CS"),
        new Book("0003","CS book 3", BigDecimal.valueOf(29.99D), "CS"),

        new Book("0004","Child book 1", BigDecimal.valueOf(17.99D), "CHILD"),
        new Book("0005","Child book 2", BigDecimal.valueOf(7.99D), "CHILD"),
        new Book("0006","Child book 3", BigDecimal.valueOf(27.99D), "CHILD"),

        new Book("0007", "AV book 1", BigDecimal.valueOf(13.99D), "AV"),
        new Book("0008", "AV book 2", BigDecimal.valueOf(3.99D), "AV"),
        new Book("0009", "AV book 3", BigDecimal.valueOf(23.99D), "AV"),
        new Book("0010", "AV book 4", BigDecimal.valueOf(33.99D), "AV")
    };

    private static final Map<String, Book> booksMap = new ConcurrentHashMap<>();

    public static Book saveBook(Book book){
        booksMap.put(book.getIsbn(), book);
        return book;
    }

    public static Optional<Book> findBookById(String isbn){
        return Optional.ofNullable(booksMap.get(isbn));
    }

    public static Collection<Book> findAllBook(){
        return booksMap.values();
    }

    public static void removeBook(Book book){
        booksMap.remove(book.getIsbn());
    }

    public static Mono<Book> findBookMonoById(String isbn){
        return Mono.justOrEmpty(findBookById(isbn));
    }

    public static Mono<Book> findBooksQuery(BookQuery bookQuery){

    }
}
