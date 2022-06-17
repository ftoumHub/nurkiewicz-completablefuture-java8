package com.reactive.cf;

import com.reactive.stackoverflow.LoadFromStackOverflowTask;
import com.reactive.util.AbstractFuturesTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class S01_Introduction extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S01_Introduction.class);

	/**
	 * Broken abstraction - blocking method calls
	 */
	@Test
	public void blockingCall() throws Exception {
		String title = client.mostRecentQuestionAbout("java");
		log.debug("Most recent Java question: '{}'", title);
	}

	@Test
	public void executorService() throws Exception {
		final Callable<String> task = () -> client.mostRecentQuestionAbout("java");
		final Future<String> javaQuestionFuture = execService.submit(task);
		//...
		String javaQuestion = javaQuestionFuture.get();
		log.debug("Found: '{}'", javaQuestion);
	}

	/**
	 * Composing is impossible
	 */
	@Test
	public void waitForFirstOrAll() throws Exception {
		final Future<String> java = findQuestionsAbout("java");
		final Future<String> scala = findQuestionsAbout("scala");

		//???
	}

	private Future<String> findQuestionsAbout(String tag) {
		final Callable<String> task = new LoadFromStackOverflowTask(client, tag);
		return execService.submit(task);
	}

}

