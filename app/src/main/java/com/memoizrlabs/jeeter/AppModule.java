package com.memoizrlabs.jeeter;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.session.DefaultSessionHandler;
import com.memoizrlabs.jeeter.session.SessionHandler;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

final class AppModule implements ShankModule {

    private static final String UI_SCHEDULER = "ui";
    private static final String IO_SCHEDULER = "io";

    @Override
    public void registerFactories() {
        Shank.registerFactory(SessionHandler.class, DefaultSessionHandler::new);

        Shank.registerNamedFactory(Scheduler.class, UI_SCHEDULER, AndroidSchedulers::mainThread);
        Shank.registerNamedFactory(Scheduler.class, IO_SCHEDULER, Schedulers::io);
    }
}
