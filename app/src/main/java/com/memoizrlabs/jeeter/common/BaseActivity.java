package com.memoizrlabs.jeeter.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import rx.subjects.PublishSubject;

public abstract class BaseActivity<V extends PresenterView> extends AppCompatActivity {

    protected final PublishSubject<Object> whenViewIsDestroyed = PublishSubject.create();
    private BasePresenter<V> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();

        onCreateView(savedInstanceState);

        presenter.onViewAttached(getView());
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            whenViewIsDestroyed.onNext(new Object());
        }

        presenter.onViewDetached();

        super.onDestroy();
    }

    @NonNull
    abstract protected BasePresenter<V> getPresenter();

    protected void onCreateView(Bundle savedInstanceState) {
    }

    @NonNull
    abstract protected V getView();
}
