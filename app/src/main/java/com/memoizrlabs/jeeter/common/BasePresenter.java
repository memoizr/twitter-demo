package com.memoizrlabs.jeeter.common;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter<V extends PresenterView> {

    private CompositeSubscription compositeSubscription;
    private V view;

    @CallSuper
    public void onViewAttached(@NonNull V view) {
        if (this.view != null) {
            throw new IllegalStateException("This presenter already has a view attached");
        }

        compositeSubscription = new CompositeSubscription();
        this.view = view;
    }

    @CallSuper
    public void onViewDetached() {
        compositeSubscription.unsubscribe();
        view = null;
    }

    protected final void unsubOnViewDetach(@NonNull Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            compositeSubscription.add(subscription);
        }
    }
}
