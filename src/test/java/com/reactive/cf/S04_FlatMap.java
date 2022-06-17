package com.reactive.cf;

import com.reactive.stackoverflow.Question;
import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletableFuture;

public class S04_FlatMap extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S04_FlatMap.class);

	@Test
	public void doubleWrappingIsWrong() throws Exception {
		// Si on appel thenApply avec un traitement qui renvoi une CompletableFuture,
		// on se retrouve à wrapper deux fois le résultat...
		final CompletableFuture<CompletableFuture<Question>> compFuture =
				javaQuestions()
						.thenApply(this::findMostInterestingQuestion);

		// Idem avec Vavr
		final Future<Future<Question>> vavrFuture = javaQuestionsVavr().map(this::findMostInterestingQuestionVavr);
	}

	@Test
	public void thenAcceptIsPoor() throws Exception {
		javaQuestions().thenAccept(document -> {
			findMostInterestingQuestion(document).thenAccept(question -> {
				googleAnswer(question).thenAccept(answer -> {
					postAnswer(answer).thenAccept(status -> {
						if (status == HttpStatus.OK.value()) {
							log.debug("OK");
						} else {
							log.error("Wrong status code: {}", status);
						}
					});
				});
			});
		});
	}

	@Test
	public void thenCompose() throws Exception {
		final CompletableFuture<Document> java = javaQuestions();

		final CompletableFuture<Question> questionFuture = java.thenCompose(this::findMostInterestingQuestion);

		final CompletableFuture<String> answerFuture = questionFuture.thenCompose(this::googleAnswer);

		final CompletableFuture<Integer> httpStatusFuture = answerFuture.thenCompose(this::postAnswer);

		httpStatusFuture.thenAccept(status -> {
			if (status == HttpStatus.OK.value()) {
				log.debug("OK");
			} else {
				log.error("Wrong status code: {}", status);
			}
		});
	}

	@Test
	public void chained() throws Exception {
		// Avec CompletableFuture
		javaQuestions()
				.thenCompose(this::findMostInterestingQuestion)
				.thenCompose(this::googleAnswer)
				.thenCompose(this::postAnswer)
				.thenAccept(status -> {
					if (status == HttpStatus.OK.value()) {
						log.debug("OK");
					} else {
						log.error("Wrong status code: {}", status);
					}
				});

		// Ou avec les Future Vavr
		javaQuestionsVavr()
				.flatMap(this::findMostInterestingQuestionVavr)
				.flatMap(this::googleAnswerVavr)
				.flatMap(this::postAnswerVavr)
				.forEach(status -> {
					if (status == HttpStatus.OK.value()) {
						log.debug("OK");
					} else {
						log.error("Wrong status code: {}", status);
					}
				});

	}

	private CompletableFuture<Document> javaQuestions() {
		return CompletableFuture.supplyAsync(() ->
						client.mostRecentQuestionsAbout("java"), execService);
	}

	private Future<Document> javaQuestionsVavr() {
		return Future.of(execService, () -> client.mostRecentQuestionsAbout("java"));
	}

	private CompletableFuture<Question> findMostInterestingQuestion(Document document) {
		return CompletableFuture.completedFuture(new Question());
	}

	private Future<Question> findMostInterestingQuestionVavr(Document document) {
		return Future.successful(new Question());
	}

	private CompletableFuture<String> googleAnswer(Question q) {
		return CompletableFuture.completedFuture("42");
	}

	private Future<String> googleAnswerVavr(Question q) {
		return Future.successful("42");
	}

	private CompletableFuture<Integer> postAnswer(String answer) {
		return CompletableFuture.completedFuture(200);
	}

	private Future<Integer> postAnswerVavr(String answer) {
		return Future.successful(200);
	}

}
