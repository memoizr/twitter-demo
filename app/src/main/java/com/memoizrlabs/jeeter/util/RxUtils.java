package com.memoizrlabs.jeeter.util;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.common.ErrorView;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;

public final class RxUtils {

    private RxUtils() {
    }

    public static <T> Observable.Transformer<T, T> handleErrorAndPerformAction(@NonNull Action1<Throwable> action) {
        return observable -> observable
                .doOnError(action)
                .onErrorResumeNext(Observable.empty());
    }

    public static <T> Observable.Transformer<T, T> handleError(@NonNull ErrorView errorView,
                                                               @NonNull Scheduler uiScheduler) {
        return observable -> observable
                .observeOn(uiScheduler)
                .doOnError(throwable -> errorView.showError())
                .onErrorResumeNext(Observable.<T>empty());
    }
}