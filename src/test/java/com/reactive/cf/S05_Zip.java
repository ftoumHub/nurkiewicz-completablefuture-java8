package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class S05_Zip extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S05_Zip.class);

	@Test
	public void thenCombine() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		final CompletableFuture<Integer> both = java.
				thenCombine(scala, (String javaTitle, String scalaTitle) ->
						javaTitle.length() + scalaTitle.length()
				);

		both.thenAccept(length -> log.debug("Total length: {}", length));
	}

	@Test
	public void thenCombineVavr() throws Exception {
		final Future<String> java = questionsVavr("java");
		final Future<String> scala = questionsVavr("scala");

		final BiFunction<String, String, Integer> concatLengthTitle =
				(String javaTitle, String scalaTitle) -> javaTitle.length() + scalaTitle.length();

		java.zipWith(scala, concatLengthTitle)
				.forEach(length -> log.debug("Total length: {}", length));
	}

	@Test
	public void either() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		final CompletableFuture<String> both = java.applyToEither(scala, String::toUpperCase);

		both.thenAccept(title -> log.debug("First: {}", title));
	}


}

