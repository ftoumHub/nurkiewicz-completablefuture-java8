package com.reactive.util;

import rx.Observable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.nonNull;

public class Futures {

	public static <T> CompletableFuture<T> fromSingleObservable(Observable<T> observable) {
		final CompletableFuture<T> future = new CompletableFuture<>();
		observable
				.doOnError(future::completeExceptionally)
				.single()
				.forEach(future::complete);
		return future;
	}

	public static <T> CompletableFuture<List<T>> fromObservable(Observable<T> observable) {
		final CompletableFuture<List<T>> future = new CompletableFuture<>();
		observable
				.doOnError(future::completeExceptionally)
				.toList()
				.forEach(future::complete);
		return future;
	}

	public static <T> Observable<T> toObservable(CompletableFuture<T> future) {
		return Observable.create(subscriber ->
				future.whenComplete((result, error) -> {
					if (nonNull(error)) {
						subscriber.onError(error);
					} else {
						subscriber.onNext(result);
						subscriber.onCompleted();
					}
				}));
	}

}
