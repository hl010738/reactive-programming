package com.spring.reactiveprogramming.day02;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

public class ReactorAPI {
    
    private static void createFluxFromExistingData(){
        Flux<Integer> justFlux = Flux.just(1, 2, 3, 4, 5, 6);
        subscribeFlux("justFlux", justFlux);
    }

    private static void  blockMono(String varName, Mono<?> mono){
        mono.doOnSubscribe(s -> System.out.print(varName + ": "))
                .doOnNext(e -> System.out.println(e + ", "))
                .block();
    }

    private static void subscribeFlux(String varName, Flux<?> flux){
        flux.doOnSubscribe(s -> System.out.print(varName + ": "))
                .doOnNext(e -> System.out.print(e + ", "))
                .doOnComplete(System.out::println)
                .subscribe();
    }
    
    private static void createFluxProgrammatically(){
        Flux<Object> generateFlux = Flux.generate(() -> 1, (state, sink) -> {
            sink.next("message #" + state);
            if (state == 10){
                sink.complete();
            }
            return state + 1;
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
    }

    public static void main(String[] args) {
        createFluxFromExistingData();
        createMonoFromExistingData();
        createFluxProgrammatically();
        createMonoAsync();
        mapVsFlatMap();
    }

}
