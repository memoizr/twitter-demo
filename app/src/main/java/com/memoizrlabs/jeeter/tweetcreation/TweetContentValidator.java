package com.memoizrlabs.jeeter.tweetcreation;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.common.ErrorView;
import com.memoizrlabs.jeeter.common.Validatable;
import com.memoizrlabs.jeeter.common.Validator;

import rx.Observable;

public final class TweetContentValidator implements Validator<String> {

    private static final int MAX_TWEET_LENGTH_CHARS = 140;
    private static final int MIN_TWEET_LENGTH_CHARS = 0;

    public Observable<String> filterValidAndShowErrorForInvalid(@NonNull String tweetContent,
                                                                @NonNull ErrorView.Validatable view) {
        return Observable.just(validate(tweetContent))
                         .doOnNext(message -> showErrorIfInvalid(view, message))
                         .filter(Validatable::isValid)
                         .map(Validatable::getContent);
    }

    @NonNull
    @Override
    public Validatable<String> validate(@NonNull String tweetContent) {
        final int length = tweetContent.length();

        final boolean isLessThanMaximumLength = length <= MAX_TWEET_LENGTH_CHARS;
        final boolean isMoreThanMinimumLength = length > MIN_TWEET_LENGTH_CHARS;
        final boolean isValid = isLessThanMaximumLength && isMoreThanMinimumLength;

        ValidityState validityState = null;

        if (!isLessThanMaximumLength) {
            validityState = StringValidityState.TOO_LONG;
        } else if (!isMoreThanMinimumLength) {
            validityState = StringValidityState.TOO_SHORT;
        }

        return Validatable.from(tweetContent, isValid, validityState);
    }

    public static void showErrorIfInvalid(@NonNull ErrorView.Validatable view,
                                          @NonNull Validatable<String> message) {
        if (!message.isValid()) {
            view.showError(message.getState());
        }
    }
}
