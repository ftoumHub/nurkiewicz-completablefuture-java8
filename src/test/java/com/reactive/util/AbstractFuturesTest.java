package com.reactive.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.reactive.stackoverflow.*;
import io.vavr.concurrent.Future;
import org.junit.jupiter.api.AfterEach;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;


public class AbstractFuturesTest {

	/**
	 * <p>Un <b>ExecutorService</b> encapsule un pool de threads et une queue de tâches à exécuter.</p>
	 *
	 * Tous les threads du pool sont toujours en cours d'exécution. Le service vérifie si
	 * une tâche est à traiter dans la queue et si c'est le cas il la retire et l'exécute.
	 * Une fois la tâche exécutée, le thread attend de nouveau que le service lui assigne
	 * une nouvelle tâche de la queue.
	 */
	protected final ExecutorService execService =
			Executors.newFixedThreadPool(10, threadFactory("Custom"));


	protected ThreadFactory threadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat + "-%d").build();
	}

	protected final StackOverflowClient stackOverflowClient = new FallbackStubClient(
			new InjectErrorsWrapper(
					new LoggingWrapper(
							new ArtificialSleepWrapper(
									new HttpStackOverflowClient()
							)
					), "php"
			)
	);

	@AfterEach
	public void stopPool() throws InterruptedException {
		execService.shutdown();
		execService.awaitTermination(10, SECONDS);
	}

	protected CompletableFuture<String> questions(String tag) {
		return supplyAsync(() -> stackOverflowClient.mostRecentQuestionAbout(tag), execService);
	}

	/**protected Future<String> questionsVavr(String tag) {
		return Future.of(execService, () -> stackOverflowClient.mostRecentQuestionAbout(tag));
	}*/

}
