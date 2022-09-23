package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * Ici, on revient sur l'exemple {@link S01_Introduction#waitForFirstOrAll}
 * pour voir comment on traiter ce cas de manière non bloquante.
 */
public class S05_Zip extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S05_Zip.class);


    @Test
    public void thenCombine() {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");

        java.thenCombine(scala,
                (javaTitle, scalaTitle) -> javaTitle.length() + scalaTitle.length()
        );

        //both.thenAccept(length -> log.debug("Total length: {}", length));
    }


    @Test
    public void either() {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");

        final CompletableFuture<String> both = java.applyToEither(scala, title -> title.toUpperCase());

        both.thenAccept(title -> log.debug("First: {}", title));
    }


    @Test
    public void allOf() {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");
        final CompletableFuture<String> clojure = questions("clojure");
        final CompletableFuture<String> groovy = questions("groovy");

        // Quel type est retourné ici ???
        CompletableFuture.allOf(java, scala, clojure, groovy);

        //allCompleted.thenApply()

        /**
         *
         * allCompleted.thenRun(() -> {
            try {
                log.debug("Loaded: {}", java.get());
                log.debug("Loaded: {}", scala.get());
                log.debug("Loaded: {}", clojure.get());
                log.debug("Loaded: {}", groovy.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("", e);
            }
        });*/
    }
}

