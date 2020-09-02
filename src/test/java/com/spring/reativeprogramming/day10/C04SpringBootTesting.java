package com.spring.reativeprogramming.day10;

import com.spring.reactiveprogramming.ReactiveProgrammingApplication;
import com.spring.reactiveprogramming.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;

@SpringBootTest(classes = ReactiveProgrammingApplication.class)
@AutoConfigureWebTestClient
public class C04SpringBootTesting {

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
                .uri("/routed-r2dbc/book")
                .bodyValue(book)
                .header("Authorization", "Basic " +
                        Base64Utils.encodeToString("admin:secret".getBytes())
                ).exchange()
                .expectStatus()
                .isCreated();
    }
}
