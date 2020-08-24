package com.spring.reactiveprogramming.day04;

import com.spring.reactiveprogramming.domain.Book;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
                // 开始发起http请求
                // 发起请求有2中方法 exchange()和retrieve()
                // retrieve方法适用于有返回数据的请求
                // 并且如果返回码不是200，会抛异常
                // exchange()方法适用于没有返回数据的请求
                // 这里post场景适用exchange()
                // post()方法会返回一个Mono以获取请求的相应信息
                .exchange()
                .doOnNext(
                        clientResponse -> System.out.println(">>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode())
                ).block();

        webClient.get().uri("/book/{isbn}", book.getIsbn())
                .retrieve()
                .bodyToMono(Book.class)
                .doOnNext(aBook -> System.out.println(">>>> Get book: " + aBook))
                .block();

        book.setPrice(BigDecimal.valueOf(39.99));
        webClient.put().uri("/book/{isbn}", book.getIsbn())
                .body(Mono.just(book), Book.class)
                .exchange()
                .doOnNext(
                        clientResponse -> System.out.println(">>>>>>>> PUT RESPONSE STATUS CODE: " + clientResponse.statusCode())
                ).block();

        webClient.get().uri("/books")
                .retrieve()
                .bodyToFlux(Book.class)
                .doOnNext(aBook -> System.out.println(">>>>>>> GET BOOKS: " + aBook))
                // 如果会有多个对象
                // blockLast方法会等待最后一个对象的返回
                .blockLast();

        webClient.delete().uri("/book/{isbn}", book.getIsbn())
                .exchange()
                .doOnNext(
                        clientResponse -> System.out.println(">>>>>>>> DELETE RESPONSE STATUS CODE: " + clientResponse.statusCode())
                ).block();

        webClient.post().uri("/book")
                .body(Mono.just(book), Book.class)
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode() != HttpStatus.CREATED){
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    System.out.println(">>>>>>>> POST RESPONSE STATUS CODE: " + clientResponse.statusCode());
                    return Mono.just(clientResponse);
                })
                // 当管道中出现异常并处理后
                // 可以retry
                .retryBackoff(3, Duration.ofSeconds(1));

        // 由于WebClint.create出来的对象默认是没有timeout
        // 也就是说没有超时断开的http连接的方法
        // 需要自行build一个带有timeout的连接实现
        var httpClient = HttpClient.create()
                .tcpConfiguration(
                        tcpClient -> {
                            // 设置连接timeout时间
                            tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                                    .doOnConnected(
                                            connection -> connection.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                                    );
                            return tcpClient;
                        }
                );

        // 封装成一个connector
        var connector = new ReactorClientHttpConnector(httpClient);

        var webClientWithHttpTimeout = WebClient.builder()
                .clientConnector(connector)
                .build();

    }
}
