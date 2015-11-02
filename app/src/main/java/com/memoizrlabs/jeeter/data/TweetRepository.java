package com.memoizrlabs.jeeter.data;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.api.model.Tweet;

import java.util.List;

import rx.Observable;

public interface TweetRepository {

    @NonNull
    Observable<List<Tweet>> getTweets(int count);
}
