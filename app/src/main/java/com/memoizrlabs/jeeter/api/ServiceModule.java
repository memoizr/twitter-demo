package com.memoizrlabs.jeeter.api;

import android.support.annotation.NonNull;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RestAdapter;
import rx.functions.Func0;

public final class ServiceModule implements ShankModule {

    private static final String ENDPOINT = "https://api.twitter.com";
    private static final String MEDIA_UPLOAD_ENDPOINT = "https://upload.twitter.com";

    @Override
    public void registerFactories() {
        Shank.registerFactory(TweetService.class, getTweetServiceFactory());
        Shank.registerFactory(MediaUploadService.class, getMediaUploadServiceFactory());
    }

    @NonNull
    private static Func0<TweetService> getTweetServiceFactory() {
        return () -> {
            final RestAdapter restAdapter = getBuilder()
                    .setEndpoint(ENDPOINT)
                    .build();

            return restAdapter.create(TweetService.class);
        };
    }

    @NonNull
    private static Func0<MediaUploadService> getMediaUploadServiceFactory() {
        return () -> {
            final RestAdapter restAdapter = getBuilder()
                    .setEndpoint(MEDIA_UPLOAD_ENDPOINT)
                    .build();

            return restAdapter.create(MediaUploadService.class);
        };
    }

    @NonNull
    private static RestAdapter.Builder getBuilder() {
        final TwitterCore core = TwitterCore.getInstance();
        final TwitterAuthConfig authConfig = core.getAuthConfig();
        final TwitterSession activeSession = core.getSessionManager().getActiveSession();
        final SSLSocketFactory sslSocketFactory = core.getSSLSocketFactory();

        final AuthenticatedClient client = new AuthenticatedClient(authConfig,
                                                                   activeSession,
                                                                   sslSocketFactory);
        return new RestAdapter
                .Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setClient(client);
    }
}
