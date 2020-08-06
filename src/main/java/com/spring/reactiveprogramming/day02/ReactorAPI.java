package com.spring.reactiveprogramming.day02;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

public class ReactorAPI {

    // 以固定的方式创建Flux
    private static void createFluxFromExistingData(){
        var justFlux = Flux.just(1, 2, 3, 4, 5, 6);
        subscribeFlux("justFlux", justFlux);
        var arrayFlux = Flux.fromArray(new Integer[]{1, 2, 3, 4, 5});
        subscribeFlux("arrayFlux", arrayFlux);
        var iterableFlux = Flux.fromIterable(Arrays.asList(1, 2, 3, 4));
        subscribeFlux("iterableFlux", iterableFlux);
        var streamFlux = Flux.fromStream(Stream.of(1, 2, 3, 4));
        subscribeFlux("streamFlux", streamFlux);
        var rangeFlux = Flux.range(1, 7);
        subscribeFlux("rangeFlux", rangeFlux);
    }

    private static void  blockMono(String varName, Mono<?> mono){
        mono.doOnSubscribe(s -> System.out.print(varName + ": "))
                .doOnNext(e -> System.out.println(e + ", "))
                // block方法与subscribe类似，都是打开管道执行代码
                // 区别在于block会阻塞线程，等待管道内代码执行完毕并返回
                .block();
    }

    private static void subscribeFlux(String varName, Flux<?> flux){
        // doOnSubscribe方法会在flux调用subscribe()方法时触发
        flux.doOnSubscribe(s -> System.out.print(varName + ": "))
                // 对流中的每一个元素都会触发一次doOnNext
                .doOnNext(e -> System.out.print(e + ", "))
                // 当流中的所有元素处理完之后
                .doOnComplete(System.out::println)
                // 这3个方法打印的内容在subscribe()调用前都不会触发
                // 也就是说在控制台上是看不到这3行代码打印的东西
                // 当subscribe()方法调用时，会将之前的代码分配给系统一个
                // 随机的线程执行.
                .subscribe();

        // subscribe的编程方法的精髓在于，在subscribe()方法之前的代码都是
        // 在做计划，当subscribe()方法调用时才真正执行
    }

    // 以编程的方法创建Flux
    // 提供一个state变量控制sink
    // sink就是创建的Flux，可以调用onNext，complete等方法
    private static void createFluxProgrammatically() {
        Flux<Object> generateFlux = Flux.generate(
                () -> 1, // 初始化state
                (state, sink) -> {
                    sink.next("message #" + state); // 发射一个event
                    if (state == 10) {
                        sink.complete(); // 所有event发射完毕
                    }
                    return state + 1; // 返回state值用于下一次调用判断
                });
        subscribeFlux("generateFlux", generateFlux);
    }

    private static void createMonoAsync(){
        var callableMono = Mono.fromCallable(() -> Thread.currentThread().getName() + " @ " + LocalDateTime.now())
                .publishOn(Schedulers.elastic());
        blockMono("callableMono", callableMono);
        var runnableMono = Mono.fromCallable(() -> Thread.currentThread().getName() + " @ " + LocalDateTime.now())
                .publishOn(Schedulers.elastic());
        blockMono("runnableMono", runnableMono);
        var supplierMono = Mono.fromCallable(() -> Thread.currentThread().getName() + " @ " + LocalDateTime.now())
                .publishOn(Schedulers.elastic());
        blockMono("supplierMono", supplierMono);
    }

    private static void createMonoFromExistingData() {
        var justMono = Mono.just(1);
        blockMono("justMono", justMono);
    }

    private static void mapVsFlatMap() {
        var mapFlux = Flux.just(1, 2 ,3).map(it -> "id # " + it);
        subscribeFlux("mapFlux", mapFlux);
        var flatMapFlux = Flux.just(1, 2, 3).flatMap(i -> Mono.just("id #" + i));
        subscribeFlux("flatMapFlux", flatMapFlux);
    }

    private static void monoFluxInterchange(){
        var monoFlux = Mono.just(1).flux();
        subscribeFlux("monoFlux", monoFlux);
        var fluxMono = Flux.just(1, 2, 3).collectList();
        blockMono("fluxMono", fluxMono);
    }

    private static void useThenForFlow(){
        var thenMono = Mono.just("world")
                .map(i -> "hello " + i)
                .doOnNext(System.out::println)
                // then用于两个管道之间的连接在同一个线程执行
                // 即当完成一个管道并转到另一个管道时
                // 如果多个管道分别subscribe，会有可能多个管道
                // 分别在不同的线程执行，并且无法保证顺序
                .thenReturn("do sth else");
        blockMono("thenMono", thenMono);
    }

    private static void zipMonoOrFlux(){
        var userId = "aaa";
        var monoProfile = Mono.just(userId + " information");
        var monoLatestOrder = Mono.just(userId + " latest order");
        var monoLatestReview = Mono.just(userId + " latest review");
        // zip 的作用是将多个在不同的线程执行的，不同的管道，合并到同一个管道在一个线程执行
        var zipMono = Mono.zip(monoProfile, monoLatestOrder, monoLatestReview)
                .doOnNext(t -> System.out.printf("%s的主页，%s, %s, %s%n", userId, t.getT1(), t.getT2(), t.getT3()));
        blockMono("zipMono", zipMono);
    }

    private static void errorHandling(){
        var throwExceptionFlux = Flux.range(1, 10)
                .map(i -> {
                    if (i > 5) {
                        //传统的方式
                        throw (new RuntimeException("sth wrong"));
                    }
                    return "item #" + i;
                });
        subscribeFlux("throwExceptionFlux", throwExceptionFlux);

        var errorFlux = Flux.range(1, 10).flatMap(i -> {
            if (i > 5) {
                // 将exception包装成Mono
                return Mono.error(new RuntimeException("sth wrong"));
            }
            return Mono.just("item #" + i);
        });
        subscribeFlux("errorFlux", errorFlux);
    }

    public static void main(String[] args) {
        createFluxFromExistingData();
//        createMonoFromExistingData();
//        createFluxProgrammatically();
//        createMonoAsync();
//        mapVsFlatMap();
//        monoFluxInterchange();
//        useThenForFlow();
//        zipMonoOrFlux();
//        errorHandling();
    }
}

