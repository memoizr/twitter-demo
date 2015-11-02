package com.memoizrlabs.jeeter.tweetcreation.text;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;

import rx.Scheduler;

public final class TweetCreationModule implements ShankModule {

    @Override
    public void registerFactories() {
        final Scheduler uiScheduler = Shank.provideNamed(Scheduler.class, "ui");

        Shank.registerFactory(TweetCreationPresenter.class, () -> {
                                  final TweetService tweetService = Shank.provide(TweetService.class);
                                  return new TweetCreationPresenter(tweetService,
                                                                    new TweetContentValidator(),
                                                                    uiScheduler);
                              });
    }
}
