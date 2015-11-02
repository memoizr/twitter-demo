package com.memoizrlabs.jeeter.tweetcreation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.memoizrlabs.jeeter.api.model.Tweet;

public final class TweetCreationHelper {

    public static final String TWEET = "tweet";

    private TweetCreationHelper() {
    }

    @Nullable
    public static Tweet getResult(@NonNull Intent intent) {
        final Bundle extras = intent.getExtras();
        return (Tweet) extras.getSerializable(TWEET);
    }
}
