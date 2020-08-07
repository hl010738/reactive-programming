package com.spring.reactiveprogramming.day04;

import com.spring.reactiveprogramming.domain.Book;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class C01WebClientShowcases {
    public static void main(String[] args) {
        var book = Book.builder().isbn(String.valueOf(System.currentTimeMillis()))
                .category("TEST")
                .title("Book from Webclient")
                .price(BigDecimal.valueOf(23.99))
                .build();

        var webClient = WebClient.create("http://localhost:8080/routed");

        webClient.post().uri("/book")
                .body(Mono.just(book), Book.class)
                .exchange()
                .doOnNext(
                        clientResponse -> System.out.println(">>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode())
                ).block();
    }
}
