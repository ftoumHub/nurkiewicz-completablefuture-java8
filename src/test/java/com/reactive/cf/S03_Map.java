package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.vavr.API.Future;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 *
 */
public class S03_Map extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S03_Map.class);

	@Test
	public void oldSchool() throws Exception {
		final CompletableFuture<Document> java = supplyAsync(() -> client.mostRecentQuestionsAbout("java"), execService);

		final Document document = java.get();       //blocks
		final Element element = document.select("a.question-hyperlink").get(0);
		final String title = element.text();
		final int length = title.length();

		log.debug("Length: {}", length);
	}

	@Test
	public void vavrOldSchool() throws Exception {
		final Future<Document> java = Future(execService, () -> client.mostRecentQuestionsAbout("java"));

		final Document document = java.get();       //blocks
		final Element element = document.select("a.question-hyperlink").get(0);
		final String title = element.text();
		final int length = title.length();

		log.debug("Length: {}", length);
	}

	/**
	 * Callback hell, pas possible d'utiliser la composition
	 */
	@Test
	public void callbacksCallbacksEverywhere() throws Exception {
		final CompletableFuture<Document> java = supplyAsync(() -> client.mostRecentQuestionsAbout("java"), execService);

		// thenAccept ne renvoi rien, pas possible d'enchainer des traitements...
		java.thenAccept(document -> log.debug("Downloaded: {}", document));
	}

	@Test
	public void thenApply() throws Exception {
		final CompletableFuture<Document> java =
				supplyAsync(() -> client.mostRecentQuestionsAbout("java"), execService);

		final CompletableFuture<Element> titleElement =
				java.thenApply((Document doc) ->
						doc.select("a.question-hyperlink").get(0));

		final CompletableFuture<String> titleText = titleElement.thenApply(Element::text);

		final CompletableFuture<Integer> length = titleText.thenApply(String::length);

		log.debug("Length: {}", length.get());
	}

	@Test
	public void thenApplyChained() throws Exception {

		final CompletableFuture<Integer> length = supplyAsync(() -> client.mostRecentQuestionsAbout("java"), execService)
				.thenApply(doc -> doc.select("a.question-hyperlink").get(0))
				.thenApply(Element::text)
				.thenApply(String::length);

		log.debug("Length: {}", length.get());
	}

	@Test
	public void vavrMapChained() throws Exception {
		final Future<Integer> length = Future.of(execService, () -> client.mostRecentQuestionsAbout("java"))
				.map(doc -> doc.select("a.question-hyperlink").get(0))
				.map(Element::text)
				.map(String::length);

		log.debug("Length: {}", length.get());
	}

}

