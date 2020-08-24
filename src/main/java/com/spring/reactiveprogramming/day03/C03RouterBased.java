package com.spring.reactiveprogramming.day03;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.reactiveprogramming.domain.Book;
import com.spring.reactiveprogramming.domain.InMemoryDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;


import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Configuration
public class C03RouterBased {
    private static final String PATH_PREFIX = "/routed";

    private final Validator validator;
    private final ObjectMapper objectMapper;

    public RouterFunction<ServerResponse> routers() {
        return RouterFunctions.route()
                .POST(PATH_PREFIX + "book", this::create)
                .build();
    }

    private Mono<ServerResponse> update(ServerRequest request){
        var isbn = request.pathVariable("isbn");
        return InMemoryDataSource.findBookMonoById(isbn)
                .flatMap(book ->
                    C04ReactiveControllerHelper
                            .requestBodyToMono(request, validator, Book.class)
                            .map(InMemoryDataSource::saveBook)
                            .flatMap(b -> ServerResponse.ok().build())
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> find(ServerRequest request) {
        var isbn = request.pathVariable("isbn");
        return InMemoryDataSource.findBookMonoById(isbn)
                .flatMap(book -> ServerResponse.ok().bodyValue(book))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<ServerResponse> findAll(ServerRequest request) {
        var books = InMemoryDataSource.findAllBooks();
        return ServerResponse.ok().bodyValue(books);
    }


    // 以下代码是不使用任何框架的情况下
    // 带有重试的save
    // 以防止网络抖动的情况出现的save异常
    private static final AtomicInteger counter = new AtomicInteger(1);

    private Mono<ServerResponse> create(ServerRequest request) {
//        if (counter.getAndIncrement() < 3) {
//            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//        counter.set(1);

        return C04ReactiveControllerHelper.requestBodyToMono(request, validator,
                (t, errors) -> InMemoryDataSource.findBookMonoById(t.getIsbn())
                        .map((book -> {
                            errors.rejectValue("isbn", "already.exists", "Already exists");
                            return Tuples.of(book, errors);
                        }))
//                (t, errors) -> {
//                    Optional<Book> theBook = InMemoryDataSource.findBookById(t.getIsbn());
//                    if (theBook.isPresent()) {
//                        errors.rejectValue("isbn", "already.exists", "Already exists");
//                    }
//                    return Tuples.of(t, errors);
//                }
                , Book.class)
                .map(InMemoryDataSource::saveBook)
                .flatMap(book -> ServerResponse.created(
                        UriComponentsBuilder.fromHttpRequest(request.exchange().getRequest())
                                .path("/").path(book.getIsbn()).build().toUri())
                        .build());
    }
}
