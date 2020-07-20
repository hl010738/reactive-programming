package com.spring.reactiveprogramming.day01;

import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.InMemoryDataSource;
import reactor.core.publisher.Flux;

import java.util.Comparator;


public class ReactiveProgramming01 {
    // 返回每个类别最贵的书，响应式编程
    public static Flux<Book> getTopPriceBookByCategoryReactive(Flux<Book> books){
        return  books.collectMultimap(Book::getCategory)
                .flatMapMany(it -> Flux.fromIterable(it.entrySet()))
                .flatMap(it -> Flux.fromIterable(it.getValue())
                                .sort(Comparator.comparing(Book::getCategory).reversed())
                                .next());
    }

    public static void main(String[] args) {
        Flux<Book> pipline = getTopPriceBookByCategoryReactive(Flux.just(InMemoryDataSource.books));
        pipline = pipline.doOnNext(System.out::println);
        System.out.println("没有事情发生，直到pipline开始");
        pipline.subscribe();

    }
}
