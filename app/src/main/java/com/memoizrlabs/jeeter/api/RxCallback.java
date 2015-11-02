package com.memoizrlabs.jeeter.api;

import android.support.annotation.NonNull;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RxCallback<T> extends Callback<T> {

    private PublishSubject<Result<T>> resultSubject = PublishSubject.create();

    @Override
    public void success(Result<T> result) {
        resultSubject.onNext(result);
    }

    @Override
    public void failure(TwitterException e) {
        resultSubject.onError(e);
    }

    @NonNull
    public Observable<Result<T>> onResult() {
        return resultSubject;
    }

    @NonNull
    public Observable<Result<T>> reset() {
        resultSubject = PublishSubject.create();
        return resultSubject;
    }
}
