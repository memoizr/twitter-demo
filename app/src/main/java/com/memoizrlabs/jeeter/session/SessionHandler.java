package com.memoizrlabs.jeeter.session;

import android.content.Context;
import android.support.annotation.NonNull;

import com.twitter.sdk.android.core.TwitterSession;

public interface SessionHandler {

    boolean isLoggedIn();
    void createSession(@NonNull Context context);
    void performLogin(@NonNull TwitterSession session);
}
