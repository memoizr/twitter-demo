package com.memoizrlabs.jeeter.stream;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.data.TweetRepository;

import rx.Scheduler;

public final class StreamModule implements ShankModule {

    @Override
    public void registerFactories() {
        Shank.registerFactory(StreamPresenter.class,
                              () -> {
                                  final Scheduler uiScheduler = Shank.provideNamed(Scheduler.class, "ui");
                                  final TweetRepository tweetRepository =
                                          Shank.provide(TweetRepository.class);
                                  return new StreamPresenter(tweetRepository,
                                                             uiScheduler);
                              });
    }
}
