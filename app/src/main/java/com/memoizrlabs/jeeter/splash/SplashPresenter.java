package com.memoizrlabs.jeeter.splash;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.PresenterView;
import com.memoizrlabs.jeeter.session.SessionHandler;

final class SplashPresenter extends BasePresenter<SplashPresenter.View> {

    private final SessionHandler sessionHandler;

    SplashPresenter(@NonNull SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        if (sessionHandler.isLoggedIn()) {
            view.showTweets();
        } else {
            view.showLogin();
        }
    }

    interface View extends PresenterView {

        void showTweets();
        void showLogin();
    }
}
