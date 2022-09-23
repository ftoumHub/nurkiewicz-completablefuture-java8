package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static com.reactive.util.Await.await;
import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Ici, on revient sur l'exemple {@link S01_Introduction#waitForFirstOrAll} pour voir
 * comment on traite cet exemple de manière non bloquante.
 */
public class S05_Zip extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S05_Zip.class);

    /**
     * Ici on introduit thenCombine, à ne pas confondre avec thenApply et thenCompose ;)
     *
     * On commence à paralléliser notre code!
     */
    @Test
    public void thenCombine() {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");

        // On revient au cas ou on doit attendre le retour de 2 futures comme dans l'introduction
        // Quel va être le type de l'expression suivante ?
                java.thenCombine(scala,
                        // Ici, les 2 paramètres sont les retours de chaque Future lorsqu'elles arrivent
                        (javaTitle, scalaTitle) -> javaTitle.length() + scalaTitle.length()
                );

        //both.thenAccept(length -> log.debug("Total length: {}", length));

    }



    /**@Test
    public void thenCombineVavr() {
        final Future<String> java = questionsVavr("java");
        final Future<String> scala = questionsVavr("scala");

        final BiFunction<String, String, Integer> concatLengthTitle =
                (String javaTitle, String scalaTitle) -> {
                    log.debug("javaTitle.length() : {}, scalaTitle.length() : {}", javaTitle.length(), scalaTitle.length());
                    return javaTitle.length() + scalaTitle.length();
                };

        java.zipWith(scala, concatLengthTitle)
                .forEach(length -> log.debug("Total length: {}", length));

        await(5000, MILLIS); // On attend dans le thread main
    }*/

    @Test
    public void either() {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");

        // La lambda passé en paramètre à applyToEither ne prend qu'un seul paramètre, pourquoi?
        final CompletableFuture<String> both = java.applyToEither(scala, title -> title.toUpperCase());

        both.thenAccept(title -> log.debug("First: {}", title));
    }


}

