package com.spring.reactiveprogramming.domain;

import java.math.BigDecimal;

public final class InMemoryDataSource {
    public static final Book[] books = new Book[]{
        new Book("CS book 1", BigDecimal.valueOf(19.99D), "CS"),
        new Book("CS book 2", BigDecimal.valueOf(9.99D), "CS"),
        new Book("CS book 3", BigDecimal.valueOf(29.99D), "CS"),

        new Book("Child book 1", BigDecimal.valueOf(17.99D), "CHILD"),
        new Book("Child book 2", BigDecimal.valueOf(7.99D), "CHILD"),
        new Book("Child book 3", BigDecimal.valueOf(27.99D), "CHILD"),

        new Book("AV book 1", BigDecimal.valueOf(13.99D), "AV"),
        new Book("AV book 2", BigDecimal.valueOf(3.99D), "AV"),
        new Book("AV book 3", BigDecimal.valueOf(23.99D), "AV"),
        new Book("AV book 4", BigDecimal.valueOf(33.99D), "AV")
    };
}
