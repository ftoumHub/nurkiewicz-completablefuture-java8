package com.reactive.cf;

import com.reactive.stackoverflow.LoadFromStackOverflowTask;
import com.reactive.util.AbstractFuturesTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Retour sur les limitations de l'interface java.util.concurrent.Future.
 */
public class S01_Introduction extends AbstractFuturesTest {

	private static final Logger log = LoggerFactory.getLogger(S01_Introduction.class);


	/**
	 *
	 * Pb, on a aucune idée de ce que fait la méthode à part retourner une String,
	 * ce qui ne nous apprend à peu près rien.
	 *
	 * De bien des manières, ce code est incomplet (même si il fonctionne) car il peut se passer un certain
	 * nombre d'impondérables qui peuvent rendre ce code dysfonctionnel.
	 * (Ex: stackoverflow est 'down', bug de parsing HTML...)
	 *
	 * En cas d'erreur réseau, le code peut tourner sans s'arrêter et donc bloquer
	 * le thread principal de l'application.
	 */
	@Test
	public void callingTaskInMainThread() {
		// Cette méthode va sur stackoverflow pour interroger la page principale
		// et récupérer la dernière question au sujet de java.
		final String title = stackOverflowClient.mostRecentQuestionAbout("java");

		log.debug("Most recent Java question: '{}'", title);
	}





	/**
	 * Pour éviter de bloquer le thread principal, on peut lancer un traitement
	 * dans un autre thread et ainsi libérer le thread initial (client) pour qu'il poursuive ses traitements.
	 *
	 * Appel de la méthode dans un thread 'custom' cf {@link AbstractFuturesTest#execService}
	 */
	@Test
	public void callingTaskInBackgroundThread() throws Exception {
		final Callable<String> task = () -> stackOverflowClient.mostRecentQuestionAbout("java");

		// Ici le code est exécuté dans un pool de thread, on obtient en retour une Future
		// Une méthode retournant une future est non bloquante
		final Future<String> javaQuestionFuture = execService.submit(task);

		// Pb, la seule façon d'interagir avec une Future est d'appeler la méthode get()
		String javaQuestion = javaQuestionFuture.get();
		//String javaQuestion = javaQuestionFuture.get(1, TimeUnit.SECONDS);
		log.debug("Found: '{}'", javaQuestion);
	}




	/**
	 * La composition n'est pas possible, si on veut attendre 2 futures...
	 */
	@Test
	public void waitForFirstOrAll() throws Exception {
		final Future<String> java = findQuestionsAbout("java");
		final Future<String> scala = findQuestionsAbout("scala");

		//???
		java.get();
		scala.get();

		// comment faire si on veut seulement le résultat de la première future retournant une valeur
		// on a une solution mais du code vraiment très peu élégant.
		while (true) {
			java.get(1, TimeUnit.MICROSECONDS);
			scala.get(1, TimeUnit.MICROSECONDS);
		}
	}

	private Future<String> findQuestionsAbout(String tag) {
		final Callable<String> task = new LoadFromStackOverflowTask(stackOverflowClient, tag);
		return execService.submit(task);
	}

}

