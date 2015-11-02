package com.memoizrlabs.jeeter.data;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.api.TweetService;

public final class DataModule implements ShankModule {

    @Override
    public void registerFactories() {
        Shank.registerFactory(TweetRepository.class,
                              () -> {
                                  final TweetService tweetService = Shank.withScope(DataModule.class)
                                                                 .provide(TweetService.class);
                                  return new DefaultTweetRepository(tweetService);
                              });
    }
}
