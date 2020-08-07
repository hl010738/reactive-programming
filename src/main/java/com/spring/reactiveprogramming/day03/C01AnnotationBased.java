package com.spring.reactiveprogramming.day03;

import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.InMemoryDataSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RequestMapping("/annotated")
@RestController
public class C01AnnotationBased {

    @GetMapping("books")
    public Flux<Book> findAll(){
       return Flux.fromIterable(InMemoryDataSource.findAllBooks());
    }

    @PostMapping("book")
    public Mono<ResponseEntity<?>> create(@Valid @RequestBody Book book,
                                          //spring自动注入
                                          //spring会对@Valid对象调用validator进行自动校验
                                          //错误信息会封装到BindingResult中
                                          BindingResult bindingResult,

                                          // 用于创建标准的restful uri
                                          // 包含标准的返回码以及头信息
                                          UriComponentsBuilder ucb)
         throws MethodArgumentNotValidException{

        Optional<Book> theBook = InMemoryDataSource.findBookById(book.getIsbn());
        if (theBook.isPresent()){
            bindingResult.rejectValue("isbn", "already.exists", "already exists");
        }
        if (bindingResult.hasErrors()){
            throw (new MethodArgumentNotValidException(
                    // https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
                    new MethodParameter(new Object(){}.getClass().getEnclosingMethod(), 0),
                    bindingResult));
        }
        InMemoryDataSource.saveBook(book);
        return Mono.just(ResponseEntity.created(ucb.path("/annotated/book/").path(book.getIsbn())
                .build()
                .toUri())
        .build());
    }

//    @GetMapping("book/{isbn}")
//    public Mono<Book> find(@PathVariable String isbn){
//        return Mono.justOrEmpty(InMemoryDataSource.findBookById(isbn));
//    }

    @GetMapping("book/{isbn}")
    public Mono<ResponseEntity<Book>> find(@PathVariable String isbn){
        Optional<Book> book = InMemoryDataSource.findBookById(isbn);
        if (book.isPresent()){
            return Mono.just(ResponseEntity. notFound().build());
        }
        return Mono.just(ResponseEntity.ok(book.get()));
    }

    @PutMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> update(@PathVariable String isbn,
                                          @RequestBody Book book){
        Optional<Book> theBook = InMemoryDataSource.findBookById(isbn);
        if (!theBook.isPresent()){
            return Mono.just(ResponseEntity.notFound().build());
        }
        InMemoryDataSource.saveBook(book);
        return Mono.just(ResponseEntity.ok().build());
    }

    @DeleteMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> remove(@PathVariable String isbn){
        Optional<Book> book = InMemoryDataSource.findBookById(isbn);
        if (!book.isPresent()){
            return Mono.just(ResponseEntity.notFound().build());
        }
        InMemoryDataSource.removeBook(book.get());
        return Mono.just(ResponseEntity.ok().build());
    }

}
