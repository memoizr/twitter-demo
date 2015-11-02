package com.memoizrlabs.jeeter.data;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.api.TweetService;

import java.util.List;

import rx.Observable;

public final class DefaultTweetRepository implements TweetRepository {

    private final TweetService tweetService;

    public DefaultTweetRepository(@NonNull TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @NonNull
    @Override
    public Observable<List<Tweet>> getTweets(int count) {
        return tweetService.getMainTimeline(count);
    }
}
