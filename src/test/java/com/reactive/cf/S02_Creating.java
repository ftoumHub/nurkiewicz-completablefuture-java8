package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static com.reactive.util.Await.await;
import static java.lang.Thread.currentThread;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.nonNull;

/**
 * Grâce à la classe CompletableFuture et aux lambdas,
 * on peut désormais écrire du code réactif non bloquant.
 * <p>
 * Mais surtout on va pouvoir le faire avec du code élégant en évitant les callbacks.
 */
public class S02_Creating extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S02_Creating.class);


    @Test
    public void firstExample() throws Exception {

        final CompletableFuture<Integer> answer = CompletableFuture.completedFuture(42);

        final int fortyTwo = answer.get(); // does not block
        log.debug("Should print 42 immediately: '{}'", fortyTwo);
    }


    /**
     * Attention, quelque chose manque dans ce code!!!
     * <p>
     * Ici, il n'y a pas d'executor service, le code est exécuté dans un pool de thread commun.
     * Dans cf{@link ForkJoinPool#commonPool()}
     * <p>
     * Heureusement, on a aussi une version qui prend en paramètre un executorService
     * {@link #supplyAsyncWithCustomExecutor}
     */
    @Test
    public void supplyAsync() throws Exception {
        final CompletableFuture<String> java =
                CompletableFuture.supplyAsync(() ->
                        stackOverflowClient.mostRecentQuestionAbout("java")
                );
        log.debug("Found '{}'", java.get()); // ici on peut bloquer pour attendre le résultat
    }













    /**
     * Couramment, on va créer une CompletableFuture à partir d'une méthode factory comme supplyAsync
     */
    @Test
    public void supplyAsyncWithWhenComplete() {
        CompletableFuture.supplyAsync(() -> stackOverflowClient.mostRecentQuestionAbout("java"))
                .whenComplete((s, e) -> {
                    if (nonNull(e)) log.error("erreur :", e);
                    else log.debug("Found: '{}'", s);
                });

        log.debug("Thread courant : {}", currentThread().getName());

        await(4000, MILLIS); // On attend dans le thread main
    }










    /**
     * Avec Custom thread pool
     */
    @Test
    public void supplyAsyncWithCustomExecutor() throws Exception {
        final CompletableFuture<String> java =
                CompletableFuture.supplyAsync(
                        () -> stackOverflowClient.mostRecentQuestionAbout("java"),
                        execService
                );
        log.debug("Found: '{}'", java.get());
    }

}

