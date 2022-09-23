package com.maif.futures;

import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContinuousCompletableFutureTest {

    private static final int DELAY = 1000;

    private AtomicLong flag = new AtomicLong();

    static ContinuousCompletableFuture<String> future;

    @BeforeAll
    public static void before() {
        future = ContinuousCompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ex) {
                log.error("Error during ContinuousCompletableFuture execution", ex);
            }
            return "successfully completed";
        });
    }

    @Test
    public void shouldReturnElapsedTime() {
        future.thenAcceptAsync(s -> {
            long t = future.getElapsedTime();
            log.info("Elapsed {} ms to receive message \"{}\"", t, s);
            flag.set(t);
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            log.error("Error awaiting Test completion", ex);
        }

        Assertions.assertTrue(flag.get() >= 0.75 * DELAY, "Future completion should be delayed");
    }

    @Test
    public void shouldOperateWithOwnExecutionTime() {
        future.thenAcceptAsync((s, t) -> {
            log.info("Elapsed {} ms to receive message \"{}\"", t, s);
            flag.set(t);
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            log.error("Error awaiting Test completion", ex);
        }

        Assertions.assertTrue(flag.get() >= 0.75 * DELAY, "Future completion should be delayed");
    }

    @Test
    public void basicContinuousCompletableFuture() throws InterruptedException {
        final ContinuousCompletableFuture<Void> cf = ContinuousCompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                log.error("Error during ContinuousCompletableFuture execution", ex);
            }
            return 42;
        }).thenAcceptAsync((i, t) -> {
            log.info("Elapsed {} ms to receive message \"{}\"", t, i);
        });

        //await().atLeast(Duration.of(3, SECONDS));
    }
}
