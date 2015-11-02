package com.memoizrlabs.jeeter.splash;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.session.SessionHandler;

public final class SplashModule implements ShankModule {

    @Override
    public void registerFactories() {
        Shank.registerFactory(SplashPresenter.class, () -> {
            final SessionHandler sessionHandler = Shank.provide(SessionHandler.class);

            return new SplashPresenter(sessionHandler);
        });
    }
}
