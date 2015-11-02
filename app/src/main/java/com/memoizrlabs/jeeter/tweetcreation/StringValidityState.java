package com.memoizrlabs.jeeter.tweetcreation;

import android.support.annotation.StringRes;

import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.common.Validator;

public enum StringValidityState implements Validator.ValidityState {

    TOO_SHORT(R.string.error_too_short),
    TOO_LONG(R.string.error_too_long);

    private final int errorMessageResId;

    StringValidityState(@StringRes int errorMessageResId) {
        this.errorMessageResId = errorMessageResId;
    }

    @Override
    public int getMessage() {
        return errorMessageResId;
    }
}
