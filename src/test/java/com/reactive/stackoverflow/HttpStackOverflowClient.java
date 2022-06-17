package com.reactive.stackoverflow;

import com.google.common.base.Throwables;
import com.reactive.util.AbstractFuturesTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpStackOverflowClient implements StackOverflowClient {

	private static final Logger log = LoggerFactory.getLogger(HttpStackOverflowClient.class);

	@Override
	public String mostRecentQuestionAbout(String tag) {
		return fetchTitleOnline(tag);
	}

	@Override
	public Document mostRecentQuestionsAbout(String tag) {
		log.debug("==> mostRecentQuestionsAbout : {}", tag);
		try {
			return Jsoup.connect("http://stackoverflow.com/questions/tagged/" + tag)
					.timeout(10000)
					.validateTLSCertificates(false)
					.get();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private String fetchTitleOnline(String tag) {
		return mostRecentQuestionsAbout(tag).select("a.question-hyperlink").get(0).text();
	}

}
