package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class S08a_ErrorHandling extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S08a_ErrorHandling.class);


    public <T> CompletableFuture<T> async(CheckedFunction0<T> func) {
        return CompletableFuture.supplyAsync(() -> Try.of(func).get(), execService);
    }

    @Test
    public void asyncTestVavr1() {
        final CompletableFuture<Integer> asyncInt = async(() -> 42);

        asyncInt.thenAccept(i -> log.debug("Success! : " + i));
    }

    @Test
    public void asyncTest2() throws Exception {
        final CompletableFuture<Object> error = async(() -> {
            throw new IllegalArgumentException("Error !!!");
        });

        // En cas d'ex
        error.thenAccept(i -> log.debug("Success! : " + i));
        error.get(); // purement pour l'exemple, on ne doit pas faire ça
    }

    @Test
    public void asyncTest3() {
        final CompletableFuture<Try<Integer>> tryCompletableFuture =
                async(() -> Try.of(() -> 42))
                        .thenApply(tryInt -> tryInt.map(i -> i * 2));

        tryCompletableFuture.thenAccept((Try i) -> log.debug("??? : " + i));
    }

    @Test
    public void asyncTest4() throws Exception {
        final CompletableFuture<Try<Object>> tryCompletableFuture = async(() -> Try.of(() -> {
            throw new IllegalArgumentException("Error !!!");
        }));

        tryCompletableFuture.thenAccept((Try i) -> log.debug("Failure : " + i));
        //tryCompletableFuture.get();
    }

    @Test
    public void asyncTest5() {
        final CompletableFuture<Try<Object>> tryCompletableFuture = async(() -> Try.of(() -> {
                    throw new IllegalArgumentException("Error !!!");
                })
                .recoverWith(throwable -> Try.of(() -> 1))
                .onFailure(err -> log.error("onFailure : " + err)));

        tryCompletableFuture.thenAccept((Try i) -> log.debug("??? : " + i));
    }
}

