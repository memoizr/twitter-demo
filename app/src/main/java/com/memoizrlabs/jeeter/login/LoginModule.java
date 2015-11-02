package com.memoizrlabs.jeeter.login;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.api.RxCallback;
import com.memoizrlabs.jeeter.session.SessionHandler;
import com.twitter.sdk.android.core.TwitterSession;

public final class LoginModule implements ShankModule {

    @Override
    public void registerFactories() {
        final SessionHandler sessionHandler = Shank.provide(SessionHandler.class);
        final RxCallback<TwitterSession> twitterSessionCallback = new RxCallback<>();

        Shank.registerFactory(LoginPresenter.class,
                              () -> new LoginPresenter(sessionHandler, twitterSessionCallback));
    }
}
