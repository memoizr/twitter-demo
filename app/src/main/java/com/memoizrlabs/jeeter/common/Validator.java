package com.memoizrlabs.jeeter.common;

import android.support.annotation.NonNull;

public interface Validator<T> {

    @NonNull
    Validatable<T> validate(@NonNull T objectToValidate);

    interface ValidityState {

        int getMessage();
    }
}

