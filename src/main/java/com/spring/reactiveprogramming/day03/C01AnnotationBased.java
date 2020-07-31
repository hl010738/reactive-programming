package com.spring.reactiveprogramming.day03;

import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.InMemoryDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/annotated")
@RestController
public class C01AnnotationBased {
    @GetMapping("books")
    public Flux<Book> findAll(){
       return Flux.fromIterable(InMemoryDataSource.)
    }
}
