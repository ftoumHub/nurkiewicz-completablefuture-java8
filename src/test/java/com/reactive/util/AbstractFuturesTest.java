package com.reactive.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.reactive.stackoverflow.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractFuturesTest.class);

	protected final ExecutorService execService = Executors.newFixedThreadPool(10, threadFactory("Custom"));

	protected ThreadFactory threadFactory(String nameFormat) {
		return new ThreadFactoryBuilder().setNameFormat(nameFormat + "-%d").build();
	}

	protected final StackOverflowClient client = new FallbackStubClient(
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
		return CompletableFuture.supplyAsync(() ->
				client.mostRecentQuestionAbout(tag), execService);
	}

	protected io.vavr.concurrent.Future<String> questionsVavr(String tag) {
		return io.vavr.concurrent.Future.of(execService, () -> client.mostRecentQuestionAbout(tag));
	}

}
