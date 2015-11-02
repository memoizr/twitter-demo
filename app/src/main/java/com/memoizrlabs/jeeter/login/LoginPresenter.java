package com.memoizrlabs.jeeter.login;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.util.RxUtils;
import com.memoizrlabs.jeeter.api.RxCallback;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.ErrorView;
import com.memoizrlabs.jeeter.common.PresenterView;
import com.memoizrlabs.jeeter.session.SessionHandler;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import rx.Observable;
import rx.subjects.BehaviorSubject;

final class LoginPresenter extends BasePresenter<LoginPresenter.View> {

    private final SessionHandler sessionHandler;
    private final RxCallback<TwitterSession> twitterSessionCallback;

    private final BehaviorSubject<Object> retrySubject = BehaviorSubject.create(new Object());

    private Observable<Result<TwitterSession>> whenResultReceived;

    LoginPresenter(@NonNull SessionHandler sessionHandler,
                   @NonNull RxCallback<TwitterSession> twitterSessionCallback) {
        this.sessionHandler = sessionHandler;
        this.twitterSessionCallback = twitterSessionCallback;
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        setupLoginRequestCallback(view);

        unsubOnViewDetach(
                retrySubject
                        .flatMap(trigger -> performLoginRequest(view))
                        .subscribe(result -> view.showTimeline())
        );
    }

    private void setupLoginRequestCallback(@NonNull View view) {
        whenResultReceived = twitterSessionCallback.onResult();
        view.setCallback(twitterSessionCallback);
    }

    @NonNull
    private Observable<Result<TwitterSession>> performLoginRequest(@NonNull View view) {
        return whenResultReceived
                .doOnNext(result -> sessionHandler.performLogin(result.data))
                .compose(RxUtils.handleErrorAndPerformAction(e -> {
                    view.showError();
                    whenResultReceived = twitterSessionCallback.reset();
                    retrySubject.onNext(new Object());
                }));
    }

    interface View extends PresenterView, ErrorView {

        void showTimeline();
        void setCallback(@NonNull Callback<TwitterSession> callback);
    }
}
