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
}
