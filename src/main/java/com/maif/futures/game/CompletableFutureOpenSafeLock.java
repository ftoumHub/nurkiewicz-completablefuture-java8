package com.maif.futures.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureOpenSafeLock {

    private static final Logger log = LoggerFactory.getLogger(CompletableFutureOpenSafeLock.class);

    public Loot openSafeLock(final Thief thief, final String victim) {

        return CompletableFuture.supplyAsync(() -> Actions.unlockTheDoor())
                .thenCompose(doorOpened ->
                        CompletableFuture.supplyAsync(() -> Actions.figureOutSafetyBoxNumber(victim))
                                .thenCombineAsync(
                                        CompletableFuture.supplyAsync(() -> Actions.hackSecretPin(victim)),
                                        (safetyBoxNumber, pin) -> Actions.openSafeLock(safetyBoxNumber, pin)
                                ).exceptionally(e -> {
                                            log.error("Something went wrong: {} Run, run, run!!", e.getMessage());
                                            return Loot.BAD;
                                        }
                                )
                ).thenApply(
                        loot -> {
                            log.info("{} gets the content of the safety box: '{}'", thief.getName(), thief.handleLoot(loot));
                            return loot;
                        }
                ).join();
    }


    public static void main(String[] args) {
        log.info("\n\n COMPLETABLE FUTURE ====");
        final Loot completableFutureLoot = new CompletableFutureOpenSafeLock().openSafeLock(Thief.PETE, "Sr. Carapapas");
        log.info("App got the loot {}", completableFutureLoot);
    }
}
