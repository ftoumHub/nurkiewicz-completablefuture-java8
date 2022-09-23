package com.maif.futures.game;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import static com.maif.futures.game.Actions.*;
import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class CompletableFutureOpenSafeLock {

    private static final Logger log = LoggerFactory.getLogger(CompletableFutureOpenSafeLock.class);

    private static final ExecutorService ex =
            Executors.newFixedThreadPool(10, threadFactory("Custom"));


    private static final ThreadFactory threadFactory(String nameFormat) {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat + "-%d").build();
    }


    public CompletableFuture<Loot> openSafeLock(final Thief thief, final String victim) {

        final Function<Loot, Loot> getTheLoot = loot -> {
            log.info("{} gets the content of the safety box: '{}'", thief.getName(), thief.handleLoot(loot));
            return loot;
        };

        final Function<Throwable, Loot> runRunRun = e -> {
            log.error("Something went wrong: {} Run, run, run!!", e.getMessage());
            return Loot.BAD;
        };

        return supplyAsync(() -> openTheDoor(), ex) // Open the door
                .thenCompose(doorOpened ->
                        supplyAsync(() -> figureOutSafetyBoxNumber(victim), ex) // Get the box Number
                                .thenCombineAsync(
                                        supplyAsync(() -> hackSecretPin(victim), ex), // Get the PIN
                                        (safetyBoxNumber, pin) -> openSafetyBox(safetyBoxNumber, pin), ex) // Open the safety box
                                .exceptionally(runRunRun))
                .thenApply(getTheLoot);
    }


    public static void main(String[] args) {
        log.info("\n\n COMPLETABLE FUTURE ====");
        new CompletableFutureOpenSafeLock()
                .openSafeLock(Thief.PETE, "Sr. Carapapas")
                .whenComplete((loot, e) -> {
                    if (isNull(e)) {
                        log.info("App got the loot {}", loot);
                        ex.shutdown();
                    }
                });

    }
}
