package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import com.reactive.util.InterruptibleTask;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class S11_Cancelling extends AbstractFuturesTest {

	private static ExecutorService myThreadPool;

	@BeforeAll
	public static void init() {
		myThreadPool = Executors.newFixedThreadPool(10);
	}

	@AfterAll
	public static void close() {
		myThreadPool.shutdownNow();
	}

	@Test
	public void shouldCancelFuture() throws InterruptedException, TimeoutException {
		//given
		InterruptibleTask task = new InterruptibleTask();
		Future future = myThreadPool.submit(task);
		task.blockUntilStarted();

		//when
		future.cancel(true);

		//then
		task.blockUntilInterrupted();
	}

	//@Ignore("Fails with CompletableFuture")
	@Test
	public void shouldCancelCompletableFuture() throws InterruptedException, TimeoutException {
		//given
		InterruptibleTask task = new InterruptibleTask();
		CompletableFuture<Void> future = CompletableFuture.supplyAsync(task, myThreadPool);
		task.blockUntilStarted();

		//when
		future.cancel(true);

		//then
		task.blockUntilInterrupted();
	}
}

