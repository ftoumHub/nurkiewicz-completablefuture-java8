package com.reactive.reactor;

import com.reactive.cf.S01_Introduction;
import com.reactive.util.AbstractFuturesTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
        final CompletableFuture<Integer> both = questions("java")
                .thenCombine(questions("scala"),
                        (javaTitle, scalaTitle) -> javaTitle.length() + scalaTitle.length()
                );

        both.thenAccept(length -> log.debug("Total length: {}", length));
    }

    @Test
    public void thenCombineReactor() {
        Mono.zip(questionR("java"), questionR("scala"))
                .map(t -> t.getT1().length() + t.getT2().length())
                .doOnNext(longueur -> log.debug("Total length: {}", longueur))
                .block();
    }
}

