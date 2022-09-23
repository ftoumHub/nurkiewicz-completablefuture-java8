package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

import static com.reactive.util.Await.await;
import static java.time.temporal.ChronoUnit.MILLIS;

public class S15_Vavr extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S15_Vavr.class);




    /**@Test
    public void vavrOldSchool() {
    final Future<Document> java = Future(execService, () -> stackOverflowClient.mostRecentQuestionsAbout("java"));

    final Document document = java.get();       //blocks
    final Element element = document.select("a.question-hyperlink").get(0);
    final String title = element.text();
    final int length = title.length();

    log.debug("Length: {}", length);
    }*/


    /**@Test
    public void vavrMapChained() {
    final Future<Integer> length =
    Future.of(execService, () -> stackOverflowClient.mostRecentQuestionsAbout("java"))
    .map(doc -> doc.select("a.question-hyperlink").get(0))
    .map(Element::text)
    .map(String::length);

    log.debug("Length: {}", length.get());
    }*/

    @Test
    public void thenCombineVavr() {
        final Future<String> java = questionsV("java");
        final Future<String> scala = questionsV("scala");

        final BiFunction<String, String, Integer> concatLengthTitle =
                (String javaTitle, String scalaTitle) -> {
                    log.debug("javaTitle.length() : {}, scalaTitle.length() : {}", javaTitle.length(), scalaTitle.length());
                    return javaTitle.length() + scalaTitle.length();
                };

        java.zipWith(scala, concatLengthTitle)
                .onComplete(length -> {
                    log.debug("Total length: {}", length.get());
                });

        await(5000, MILLIS); // On attend dans le thread main
    }
}
