package com.memoizrlabs.jeeter.util;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public final class RxEditText implements TextWatcher {

    private final BehaviorSubject<String> textSubject = BehaviorSubject.create();
    private final EditText editText;

    private RxEditText(@NonNull EditText editText) {
        this.editText = editText;
    }

    @NonNull
    public static Observable<String> textChanges(@NonNull EditText editText) {
        final RxEditText rxEditText = new RxEditText(editText);
        editText.addTextChangedListener(rxEditText);
        return rxEditText.getChanges();
    }

    @NonNull
    private Observable<String> getChanges() {
        return textSubject;
    }

    @Override
    public void afterTextChanged(Editable s) {
        textSubject.onNext(editText.getText()
                                   .toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
