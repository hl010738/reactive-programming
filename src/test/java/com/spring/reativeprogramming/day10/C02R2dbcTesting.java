package com.spring.reativeprogramming.day10;

import com.spring.reactiveprogramming.ReactiveProgrammingApplication;
import com.spring.reactiveprogramming.day05.C01BookRepository;
import com.spring.reactiveprogramming.day05.C03R2dbcConfiguration;
import com.spring.reactiveprogramming.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                ReactiveProgrammingApplication.class,
                C03R2dbcConfiguration.class,
                C01BookRepository.class
        }
)

@DataR2dbcTest
public class C02R2dbcTesting {

    @Autowired
    private C01BookRepository bookRepository;

    @Test
    public void testInsert() {
        var book = Book.builder()
                .category("title category")
                .isbn("1234567")
                .price(BigDecimal.valueOf(19.99))
                .title("test title")
                .build();
        var mono = bookRepository.insert(book)
                .then(bookRepository.findById("1234567"));
        StepVerifier.create(mono)
                .expectNext(book)
                .expectComplete()
                .verify();
    }

}
