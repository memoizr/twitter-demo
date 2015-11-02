package com.memoizrlabs.jeeter.common;

import android.support.annotation.NonNull;

public final class Validatable<T> {

    private T content;
    private boolean isValid;
    private Validator.ValidityState validityState;

    private Validatable() {
    }

    public static <T> Validatable<T> from(@NonNull T content, boolean isValid, @NonNull Validator.ValidityState validityState) {
        final Validatable<T> validatable = new Validatable<>();

        validatable.content = content;
        validatable.isValid = isValid;
        validatable.validityState = validityState;

        return validatable;
    }

    public T getContent() {
        return content;
    }

    public boolean isValid() {
        return isValid;
    }

    public Validator.ValidityState getState() {
        return validityState;
    }
}
