package com.memoizrlabs.jeeter.tweetcreation.video;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BasePresenterTest;
import com.memoizrlabs.jeeter.api.MediaUploadService;
import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.api.model.MediaUploadEntity;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.tweetcreation.StringValidityState;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.TimeUnit;

import retrofit.mime.TypedFile;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({VideoRecorder.class, VideoProcessor.class, MediaUploadEntity.class, Tweet.class})
public class VideoTweetCreationPresenterTest extends
        BasePresenterTest<VideoTweetCreationPresenter, VideoTweetCreationPresenter.View> {

    private static final int MAX_TWEET_LENTGTH_CHARACTERS = 140;
    private static final String FILE_SIZE = "42";
    private static final String MEDIA_ID = "foo";

    private final PublishSubject<Object> recordingStartedSubject = PublishSubject.create();
    private final PublishSubject<Object> viewStartedSubject = PublishSubject.create();
    private final PublishSubject<Object> viewPausedSubject = PublishSubject.create();
    private final PublishSubject<String> tweetContentSubject = PublishSubject.create();
    private final PublishSubject<MediaUploadEntity> tweetServiceInitSubject = PublishSubject.create();
    private final PublishSubject<Object> tweetServiceAppendSubject = PublishSubject.create();
    private final PublishSubject<MediaUploadEntity> tweetServiceFinalizeSubject = PublishSubject.create();
    private final PublishSubject<Tweet> tweetCreationSubject = PublishSubject.create();

    private final Scheduler uiScheduler = Schedulers.immediate();
    private final TestScheduler backgroundScheduler = Schedulers.test();

    @Mock private VideoRecorder videoRecorder;
    @Mock private VideoProcessor videoProcessor;
    @Mock private MediaUploadService mediaUploadService;
    @Mock private TweetService tweetService;
    @Mock private MediaUploadEntity mediaUploadEntity;
    @Mock private TypedFile typedFile;
    @Mock private Tweet tweet;
    @Mock private VideoRecorder.CameraWrapper cameraWrapper;

    @Override
    protected void setupMocks() {
        when(view.whenStartRecording()).thenReturn(recordingStartedSubject);
        when(view.whenViewStarted()).thenReturn(viewStartedSubject.startWith(new Object()));
        when(view.whenViewPaused()).thenReturn(viewPausedSubject);
        when(view.showTweetTextEntryField()).thenReturn(tweetContentSubject);
        when(view.startCamera()).thenReturn(cameraWrapper);

        when(mediaUploadEntity.getMediaIdString()).thenReturn(MEDIA_ID);
        when(videoProcessor.getFileSize()).thenReturn(FILE_SIZE);
        when(videoProcessor.getTypedFile()).thenReturn(typedFile);

        when(mediaUploadService.uploadVideoInit(anyString(), anyString(), anyString())).thenReturn(tweetServiceInitSubject);

        when(mediaUploadService.uploadVideoAppend(anyString(),
                                                  anyString(),
                                                  any(TypedFile.class),
                                                  anyInt())).thenReturn(tweetServiceAppendSubject);

        when(mediaUploadService.uploadVideoFinalize(anyString(), anyString()))
                .thenReturn(tweetServiceFinalizeSubject);

        when(tweetService.postUpdate(anyString(), anyString())).thenReturn(tweetCreationSubject);
    }

    @NonNull
    @Override
    protected VideoTweetCreationPresenter createPresenter() {
        return new VideoTweetCreationPresenter(videoRecorder,
                                               videoProcessor,
                                               mediaUploadService,
                                               tweetService,
                                               new TweetContentValidator(),
                                               uiScheduler,
                                               backgroundScheduler);
    }

    @NonNull
    @Override
    protected VideoTweetCreationPresenter.View createView() {
        return Mockito.mock(VideoTweetCreationPresenter.View.class);
    }

    @Test
    public void when_viewStarted_then_startCamera() throws Exception {
        when(view.whenViewStarted()).thenReturn(viewStartedSubject);

        onViewAttached();

        viewStartedSubject.onNext(null);

        verify(view).startCamera();
    }

    @Test
    public void when_viewIsPaused_then_stopCamera() throws Exception {
        onViewAttached();

        viewPausedSubject.onNext(null);

        verify(view).stopCamera();
    }

    @Test
    public void when_videoRecordingIsStarted_then_startRecording() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        verify(videoRecorder).startRecording(any(VideoRecorder.CameraWrapper.class));
    }

    @Test
    public void when_videoRecordingIsStarted_then_showRecordingStarted() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        verify(view).showRecordingStarted();
    }

    @Test
    public void when_videoRecordingReachesFiveSeconds_then_stopRecording() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        verify(videoRecorder).stopRecording();
    }

    @Test
    public void when_videoRecordingReachesFiveSeconds_then_showRecordingStopped() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        verify(view).showRecordingStopped();
    }

    @Test
    public void when_eachSecondOfRecordingPasses_then_updateUICounterWithRemainingTime() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        final int totalTime = 5;

        backgroundScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        verify(view).showRemainingTime(totalTime);

        for (int i = totalTime - 1; i > 0; i--) {

            backgroundScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
            verify(view).showRemainingTime(i);
        }
    }

    @Test
    public void when_videoHasBeenRecorded_then_startProcessingVideo() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(4, TimeUnit.SECONDS);

        // too early
        verify(videoProcessor, never()).processVideo(anyString());

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        // just right
        verify(videoProcessor).processVideo(anyString());
    }

    @Test
    public void when_tweetHasBeenCreated_then_uploadVideoTweet() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        verify(mediaUploadService).uploadVideoInit("INIT", "video/mp4", FILE_SIZE);
    }

    @Test
    public void when_whenInitRequestSucceeds_then_uploadMedia() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();

        verify(mediaUploadService).uploadVideoAppend("APPEND", MEDIA_ID, typedFile, 0);
    }

    private void initMediaUpload() {
        tweetServiceInitSubject.onNext(mediaUploadEntity);
    }

    @Test
    public void when_whenAppendRequest_then_finalizeUpload() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();
        appendMedia();

        verify(mediaUploadService).uploadVideoFinalize("FINALIZE", MEDIA_ID);
    }

    private void appendMedia() {
        tweetServiceAppendSubject.onNext(null);
    }

    @Test
    public void when_whenFinalizeSucceeds_then_sendTweet() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();
        appendMedia();
        finalizeUpload();
        tweetContentSubject.onNext("hello");
        tweetCreationSubject.onNext(tweet);

        verify(tweetService).postUpdate("hello", MEDIA_ID);
    }

    private void finalizeUpload() {
        tweetServiceFinalizeSubject.onNext(mediaUploadEntity);
    }

    @Test
    public void when_whenFinalizeSucceeds_withTooShortTweet_then_showTooShortError() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();
        appendMedia();
        finalizeUpload();
        tweetContentSubject.onNext("");

        verify(view).showError(StringValidityState.TOO_SHORT);
    }

    @Test
    public void when_whenFinalizeSucceeds_withTooLongTweet_then_showTooLongError() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();
        appendMedia();
        finalizeUpload();
        tweetContentSubject.onNext(buildLongString());

        verify(view).showError(StringValidityState.TOO_LONG);
    }

    @NonNull
    private static String buildLongString() {
        final StringBuilder longStringBuilder = new StringBuilder();
        for (int i = 0; i <= MAX_TWEET_LENTGTH_CHARACTERS; i++) {
            longStringBuilder.append("a");
        }
        return longStringBuilder.toString();
    }

    @Test
    public void when_uploadIsDone_andTweetIsCreated_then_showTweetInTimeline() throws Exception {
        onViewAttached();

        recordingStartedSubject.onNext(null);

        backgroundScheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        initMediaUpload();
        appendMedia();
        finalizeUpload();
        tweetContentSubject.onNext("hello");
        tweetCreationSubject.onNext(tweet);

        verify(view).showTweetInTimeline(tweet);
    }
}