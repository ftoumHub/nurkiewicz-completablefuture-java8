package com.maif.futures;

import java.util.concurrent.*;
import java.util.function.*;

import static java.lang.System.nanoTime;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor(access = PRIVATE)
public class ContinuousCompletableFuture<T> extends CompletableFuture<T> {

    @Delegate
    private final CompletableFuture<T> baseFuture;

    private final long creationTime;

    public static <U> ContinuousCompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return new ContinuousCompletableFuture<>(CompletableFuture.supplyAsync(supplier));
    }

    private ContinuousCompletableFuture(CompletableFuture<T> baseFuture) {
        this.baseFuture = baseFuture;
        this.creationTime = nanoTime();
    }

    public Long getElapsedTime() {
        return (nanoTime() - creationTime) / 1000_000L;
    }

    public ContinuousCompletableFuture<Void> thenAcceptAsync(BiConsumer<? super T, Long> action) {
        return new ContinuousCompletableFuture<>(
                baseFuture.thenAcceptAsync(t -> action.accept(t, getElapsedTime())), creationTime);
    }


    /**@Test public void continuousSupplyAsync() {
    log.info("Starting stackOverflow request : \"java\"");

    ContinuousCompletableFuture<String> ccf =
    ContinuousCompletableFuture.supplyAsync(() -> stackOverflowClient.mostRecentQuestionAbout("java"));

    ccf.thenApply(s -> Tuple.of(s, ccf.getElapsedTime()))
    .whenComplete((t, e) -> {
    if (nonNull(e)) {
    log.error("erreur :", e);
    } else {
    log.info("Elapsed {} ms to receive message \"{}\"", t._2, t._1);
    }
    });

    log.debug("Thread courant : {}", currentThread().getName());

    await(2000, MILLIS); // On attend dans le thread main
    }*/
}
