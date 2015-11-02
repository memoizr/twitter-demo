package com.memoizrlabs.jeeter.tweetcreation.video;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.api.MediaUploadService;
import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.api.model.MediaUploadEntity;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.ErrorView;
import com.memoizrlabs.jeeter.common.PresenterView;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;

final class VideoTweetCreationPresenter extends BasePresenter<VideoTweetCreationPresenter.View> {

    private static final long RECORDING_TIME_SECONDS = 5;
    private static final int CLOCK_TICK_INTERVAL = 1;

    private final VideoRecorder videoRecorder;
    private final VideoProcessor videoProcessor;
    private final MediaUploadService mediaUploadService;
    private final TweetService tweetService;
    private final TweetContentValidator tweetContentValidator;
    private final Scheduler uiScheduler;
    private final Scheduler backgroundScheduler;

    VideoTweetCreationPresenter(@NonNull VideoRecorder videoRecorder,
                                @NonNull VideoProcessor videoProcessor,
                                @NonNull MediaUploadService mediaUploadService,
                                @NonNull TweetService tweetService,
                                @NonNull TweetContentValidator tweetContentValidator,
                                @NonNull Scheduler uiScheduler,
                                @NonNull Scheduler backgroundScheduler) {
        this.videoRecorder = videoRecorder;
        this.videoProcessor = videoProcessor;
        this.mediaUploadService = mediaUploadService;
        this.tweetService = tweetService;
        this.tweetContentValidator = tweetContentValidator;
        this.uiScheduler = uiScheduler;
        this.backgroundScheduler = backgroundScheduler;
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        final Observable<Object> recordingObservable =
                view.whenViewStarted()
                    .map(x -> view.startCamera())
                    .flatMap(cameraWrapper -> startAndStopRecordingProcess(view, cameraWrapper))
                    .share();

        final Observable<MediaUploadEntity> whenVideoIsProcessedAndUploaded =
                recordingObservable
                        .observeOn(backgroundScheduler)
                        .doOnNext(x -> videoProcessor.processVideo(videoRecorder.getVideoPath()))
                        .flatMap(x -> initUpload())
                        .flatMap(this::appendMedia)
                        .flatMap(this::finalizeUpload);

        final Observable<String> whenTweetTextIsReady =
                recordingObservable
                        .flatMap(x -> view.showTweetTextEntryField())
                        .flatMap(content -> tweetContentValidator
                                .filterValidAndShowErrorForInvalid(content, view));

        unsubOnViewDetach(
                stopCameraOnViewPaused(view),
                Observable.zip(whenTweetTextIsReady, whenVideoIsProcessedAndUploaded, VideoTweet::new)
                          .flatMap(videoTweet -> tweetService.postUpdate(
                                  videoTweet.getContent(),
                                  videoTweet.getMediaUploadEntity()
                                            .getMediaIdString()))
                          .doOnNext(view::showTweetInTimeline)
                          .subscribe()
        );
    }

    @NonNull
    private Observable<Object> startAndStopRecordingProcess(@NonNull View view,
                                                            VideoRecorder.CameraWrapper cameraWrapper) {
        return view.whenStartRecording()
                   .doOnNext(x -> videoRecorder.startRecording(cameraWrapper))
                   .doOnNext(x -> view.showRecordingStarted())
                   .flatMap(x -> startAndShowCountdown(view))
                   .doOnNext(x -> stopRecording(view));
    }

    @NonNull
    private Observable<MediaUploadEntity> initUpload() {
        return mediaUploadService
                .uploadVideoInit(MediaUploadService.Command.INIT,
                                 VideoRecorder.VideoType.VIDEO_MP4,
                                 videoProcessor.getFileSize());
    }

    private static Subscription stopCameraOnViewPaused(@NonNull View view) {
        return view.whenViewPaused()
                   .subscribe(s -> view.stopCamera());
    }

    @NonNull
    private Observable<Object> startAndShowCountdown(@NonNull View view) {
        return getCountDownObservable().share()
                                       .observeOn(uiScheduler)
                                       .doOnNext(view::showRemainingTime)
                                       .toList()
                                       .map(list -> new Object())
                                       .delay(CLOCK_TICK_INTERVAL, TimeUnit.SECONDS, backgroundScheduler)
                                       .observeOn(uiScheduler);
    }

    private void stopRecording(@NonNull View view) {
        view.showRecordingStopped();
        videoRecorder.stopRecording();
    }

    @NonNull
    private Observable<Long> getCountDownObservable() {
        final int initialDelay = 0;
        final Observable<Long> interval = Observable.interval(initialDelay,
                                                              CLOCK_TICK_INTERVAL,
                                                              TimeUnit.SECONDS,
                                                              backgroundScheduler);

        return interval.map(elapsedTime -> RECORDING_TIME_SECONDS - elapsedTime)
                       .take((int) RECORDING_TIME_SECONDS);
    }

    @NonNull
    private Observable<MediaUploadEntity> finalizeUpload(@NonNull MediaUploadEntity mediaUploadEntity) {
        return mediaUploadService.uploadVideoFinalize(
                MediaUploadService.Command.FINALIZE,
                mediaUploadEntity.getMediaIdString());
    }

    @NonNull
    private Observable<MediaUploadEntity> appendMedia(@NonNull MediaUploadEntity mediaUploadEntity) {
        final int segmentIndex = 0;
        return mediaUploadService.uploadVideoAppend(
                MediaUploadService.Command.APPEND,
                mediaUploadEntity.getMediaIdString(),
                videoProcessor.getTypedFile(), segmentIndex)
                                 .map(x -> mediaUploadEntity);
    }

    interface View extends PresenterView, ErrorView, ErrorView.Validatable {

        @NonNull
        Observable<Object> whenStartRecording();
        @NonNull
        Observable<Object> whenViewStarted();
        @NonNull
        Observable<Object> whenViewPaused();
        @NonNull
        VideoRecorder.CameraWrapper startCamera();
        @NonNull
        Observable<String> showTweetTextEntryField();

        void showRemainingTime(long remainingSeconds);
        void stopCamera();
        void showRecordingStarted();
        void showRecordingStopped();
        void showTweetInTimeline(Tweet tweet);
    }

    static class VideoTweet {

        private final String content;
        private final MediaUploadEntity mediaUploadEntity;

        VideoTweet(String content, MediaUploadEntity mediaUploadEntity) {
            this.content = content;
            this.mediaUploadEntity = mediaUploadEntity;
        }

        private String getContent() {
            return content;
        }

        private MediaUploadEntity getMediaUploadEntity() {
            return mediaUploadEntity;
        }
    }
}
