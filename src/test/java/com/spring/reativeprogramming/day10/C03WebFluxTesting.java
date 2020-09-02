package com.spring.reativeprogramming.day10;

import com.spring.reactiveprogramming.ReactiveProgrammingApplication;
import com.spring.reactiveprogramming.day03.C03RouterBased;
import com.spring.reactiveprogramming.day03.C05GlobalErrorWebExceptionHandler;
import com.spring.reactiveprogramming.day06.C01SecurityConfigurer;
import com.spring.reactiveprogramming.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ReactiveProgrammingApplication.class,
                C03RouterBased.class,
                C05GlobalErrorWebExceptionHandler.class,
                C01SecurityConfigurer.class
        }
)
@WebFluxTest
public class C03WebFluxTesting {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testWebFlux() {
        var book = Book.builder()
                .category("title category")
                .isbn("1234567")
                .price(BigDecimal.valueOf(19.99))
                .title("test title")
                .build();
        webTestClient.post()
                .uri("/routed/book")
                .bodyValue(book)
                .header("Authorization", "Basic " +
                        Base64Utils.encodeToString("admin:secret".getBytes())
                ).exchange()
                .expectStatus()
                .isCreated();
    }
}
