package com.memoizrlabs.jeeter.tweetcreation.video;

import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModule;
import com.memoizrlabs.jeeter.api.MediaUploadService;
import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;

import rx.Scheduler;

public final class VideoTweetCreationModule implements ShankModule {

    private final String cacheDir;

    public VideoTweetCreationModule(@NonNull String cacheDir) {
        this.cacheDir = cacheDir;
    }

    @Override
    public void registerFactories() {
        Shank.registerFactory(VideoTweetCreationPresenter.class, () -> {
            final Scheduler uiScheduler = Shank.provideNamed(Scheduler.class, "ui");
            final Scheduler backgroundScheduler = Shank.provideNamed(Scheduler.class, "io");
            final TweetService tweetService = Shank.provide(TweetService.class);
            final MediaUploadService mediaUploadService = Shank.provide(MediaUploadService.class);

            return new VideoTweetCreationPresenter(new VideoRecorder(cacheDir),
                                                   new VideoProcessor(cacheDir),
                                                   mediaUploadService,
                                                   tweetService,
                                                   new TweetContentValidator(),
                                                   uiScheduler,
                                                   backgroundScheduler);
        });

        Shank.registerFactory(MediaRecorder.class, MediaRecorder::new);
    }
}
