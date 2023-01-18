package com.reactive.cf;

import com.reactive.util.AbstractFuturesTest;
import io.vavr.concurrent.Future;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static io.vavr.API.Future;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * On peut comparer les 5 méthodes de l'interface {@link java.util.concurrent.Future}
 * Aux méthodes de la classe {@link CompletableFuture}
 *
 * Avec java 8 l'opération map s'appelle thenApply.
 * Vavr utilise bien le terme "map".
 */
public class S03_Map_thenApply extends AbstractFuturesTest {

    private static final Logger log = LoggerFactory.getLogger(S03_Map_thenApply.class);

    @Test
    public void oldSchoolWay() throws Exception {
        final CompletableFuture<Document> java = // Ici on a une future de Document!
                supplyAsync(() ->
                        stackOverflowClient.mostRecentQuestionsAbout("java"),
                        execService
                );

        final Document document = java.get();  // Bloquant, on voudrait éviter ça!
        final Element element = document.select("a.question-hyperlink").get(0);
        final String title = element.text();
        final int length = title.length();

        log.debug("Length: {}", length);
    }


    /**
     *
     * Callback hell, pas possible d'utiliser la composition.
     * Ça marche, mais on peut faire mieux : cf {@link #thenApply()}
     */
    @Test
    public void callbacksCallbacksEverywhere() {
        final CompletableFuture<Document> java =
                supplyAsync(() -> stackOverflowClient.mostRecentQuestionsAbout("java"), execService);

        // thenAccept ne renvoi rien, pas possible d'enchainer des traitements...
        java.thenAccept((Document document) ->
                log.debug("Downloaded: {}", document));
    }


    /**
     * thenApply va permettre d'appliquer une fonction et de retourner une valeur.
     */
    @Test
    public void thenApply() throws Exception {
        final CompletableFuture<Document> java =
                supplyAsync(() ->
                        stackOverflowClient.mostRecentQuestionsAbout("java"),
                        execService
                );

        // Là on commence à rentrer dans le monde réactif!!
        final CompletableFuture<Element> titleElement =
                java.thenApply((Document doc) -> doc.select("a.question-hyperlink").get(0)); //

        final CompletableFuture<String> titleText =
                titleElement.thenApply(Element::text);

        final CompletableFuture<Integer> length =
                titleText.thenApply(String::length);

        log.debug("Length: {}", length.get());
    }



	/**
	 * {@link CompletableFuture#thenApply(Function)} va donc permettre de chainer des traitements,
	 * comme si on écrivait du code java "procédurale".
	 */
    @Test
    public void thenApplyChained() throws Exception {
        final CompletableFuture<Document> java =
                supplyAsync(() -> stackOverflowClient.mostRecentQuestionsAbout("java"), execService);

        CompletableFuture<Integer> length = java
                        .thenApply(doc -> doc.select("a.question-hyperlink").get(0))
                        .thenApply(Element::text)
                        .thenApply(String::length);

        log.debug("Length: {}", length.get()); // Le code n'est plus bloquant jusqu'à ce qu'on appel get()
    }
}

