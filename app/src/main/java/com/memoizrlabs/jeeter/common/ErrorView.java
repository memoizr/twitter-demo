package com.memoizrlabs.jeeter.common;

import android.support.annotation.NonNull;

public interface ErrorView {

    void showError();
    interface Validatable {
        void showError(@NonNull Validator.ValidityState invalidState);
    }
}
