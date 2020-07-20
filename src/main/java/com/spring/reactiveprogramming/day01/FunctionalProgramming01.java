package com.spring.reactiveprogramming.day01;

import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.InMemoryDataSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionalProgramming01 {

    // 返回每个类别最贵的书，函数式编程
    public static List<Book> getTopPriceBookByCategoryFunctional(){
        return Stream.of(InMemoryDataSource.books)
                .collect(Collectors.groupingBy(Book::getCategory))
                .entrySet()
                .stream()
                                                    // 这里排序体现了声明式编程
                                                    // 只声明了按价格排序
                                                    // 并没有编写排序逻辑
                .map(it -> it.getValue().stream().sorted(Comparator.comparing(Book::getPrice)
                        .reversed()).findFirst().get())
                .collect(Collectors.toList());
    }

    // 返回每个类别最贵的书，非函数式编程
    public static List<Book> getTopPriceBookByCategory(){
        Map map = new HashMap<String, Book>();
        for (Book book: InMemoryDataSource.books){
            Object abook = map.get(book.getCategory());
            if (null != abook){
                // 需要编写排序逻辑
                if (book.getPrice().compareTo(((Book)abook).getPrice()) > 0){
                    map.put(book.getCategory(), book);
                }
            } else {
                map.put(book.getCategory(), book);
            }
        }
        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        List<Book> books = FunctionalProgramming01.getTopPriceBookByCategory();
        books.stream().forEach(System.out::println);

        System.out.println("-------------------");

        List<Book> bookss = FunctionalProgramming01.getTopPriceBookByCategoryFunctional();
        bookss.stream().forEach(System.out::println);

    }
}
