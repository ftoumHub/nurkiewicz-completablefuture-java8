package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * Que se passe-t-il lorsqu'une exception survient dans un traitement background?
 * <p>
 * Une future peut avoir 2 résultats : un objet ou une exception
 */
public class S08_ErrorHandling extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S08_ErrorHandling.class);


    /**
     * Ici une exception va être levée :
     * {@link com.reactive.stackoverflow.InjectErrorsWrapper#throwIfBlackListed(String)}
     */
    @Test
    public void exceptionsShortCircuitFuture() throws Exception {
        final CompletableFuture<String> questions = questions("php")
                .thenApply(r -> {
                    log.debug("Success!");
                    return r;
                });
        // on doit appeler explicitement get() pour avoir une exception dans le thread client, on est plus réactif.
        questions.get();
    }


    @Test
    public void handleExceptions() throws Exception {
        //given
        final CompletableFuture<String> questions = questions("php");

        //when
        final CompletableFuture<String> recovered = questions
                .handle((result, throwable) -> { // fonctionne comme thenApply mais en donnant accès à l'exception
                    if (throwable != null) {
                        // En cas d'exception, on peut quand même retourner une valeur
                        return "No PHP today due to: " + throwable;
                    } else {
                        return result.toUpperCase();
                    }
                });

        //then
        log.debug("Handled: {}", recovered.get());
    }

    @Test
    public void shouldHandleExceptionally() throws Exception {
        //given
        final CompletableFuture<String> questions = questions("php");

        //when
        final CompletableFuture<String> recovered = questions.exceptionally(throwable -> "Sorry, try again later");

        //then
        log.debug("Done: {}", recovered.get());
    }



    @Test
    public void asyncTestVavr1() {
        final CompletableFuture<Integer> asyncInt = async(() -> 42);

        asyncInt.thenAccept(i -> log.debug("Success! : " + i));
    }

    @Test
    public void asyncTest2() throws Exception {
        final CompletableFuture<Object> error = async(() -> { throw new IllegalArgumentException("Error !!!");});

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


    public <T> CompletableFuture<T> async(CheckedFunction0<T> func) {
        return CompletableFuture.supplyAsync(() -> Try.of(func).get(), execService);
    }
}

