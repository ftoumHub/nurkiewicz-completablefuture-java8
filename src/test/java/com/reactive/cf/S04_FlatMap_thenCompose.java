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

/**
 * Avec java 8 l'opération flatMap s'appelle thenCompose.
 * vavr utilise bien le terme "flatMap".
 */
public class S04_FlatMap_thenCompose extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S04_FlatMap_thenCompose.class);


    /**
     * Quel va être le type de retour de ce code??
     */
    @Test
    public void thenApplyIsWrongInSomeCases() {
        javaQuestions()
                .thenApply(doc ->
                        findMostInterestingQuestion(doc));
    }


    /**
     * Mauvaise pratique!
     */
    @Test
    public void thenAcceptIsPoor() {
        javaQuestions().thenAccept(document ->
                findMostInterestingQuestion(document).thenAccept(question ->
                        googleAnswer(question).thenAccept(answer ->
                                postAnswer(answer).thenAccept(status -> {
                                    if (status == HttpStatus.OK.value()) {
                                        log.debug("OK");
                                    } else {
                                        log.error("Wrong status code: {}", status);
                                    }
                                })
                        )
                )
        );
    }

    /**
     * thenCompose permet de travailler avec des méthodes retournant CompletableFuture
     */
    @Test
    public void thenCompose() {
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

    /**
     * Pipeline de tâches asynchrones, on peut maintenant chainer des tâches asynchrones!
     */
    @Test
    public void chained() {
        javaQuestions() // Avec CompletableFuture<Document>
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

        // Question, comment se propage les exceptions
    }

    /**@Test
    public void chainedWithVavr() {
        javaQuestionsVavr() // Ou avec les Future<Document> Vavr
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
    }*/


    private CompletableFuture<Document> javaQuestions() {
        return CompletableFuture.supplyAsync(() ->
                stackOverflowClient.mostRecentQuestionsAbout("java"), execService);
    }

    private Future<Document> javaQuestionsVavr() {
        return Future.of(execService, () -> stackOverflowClient.mostRecentQuestionsAbout("java"));
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
