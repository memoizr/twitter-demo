package com.memoizrlabs.jeeter.session;

import android.content.Context;
import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BuildConfig;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;

public final class DefaultSessionHandler implements SessionHandler {

    @Override
    public boolean isLoggedIn() {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        return session != null && !session.getAuthToken().isExpired();
    }

    @Override
    public void createSession(@NonNull Context context) {
        final String tweeterConsumerKey = BuildConfig.TWEETER_CONSUMER_KEY;
        final String tweeterConsumerSecret = BuildConfig.TWEETER_CONSUMER_SECRET;
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(tweeterConsumerKey, tweeterConsumerSecret);
        Fabric.with(context, new TwitterCore(authConfig));
    }

    @Override
    public void performLogin(@NonNull TwitterSession session) {
        TwitterCore.getInstance()
                   .getSessionManager()
                   .setActiveSession(session);
    }
}
