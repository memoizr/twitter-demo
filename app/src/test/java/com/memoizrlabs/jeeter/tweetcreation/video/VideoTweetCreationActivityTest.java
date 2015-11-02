package com.memoizrlabs.jeeter.tweetcreation.video;

import android.app.Activity;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.BaseRobolectricTest;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.tweetcreation.TweetCreationHelper;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@PrepareForTest({VideoTweetCreationPresenter.class, Tweet.class})
public class VideoTweetCreationActivityTest extends BaseRobolectricTest {

    private final Tweet tweet = new Tweet(null, "", "", null);

    private VideoTweetCreationActivity activity;
    private VideoTweetCreationPresenter presenter;

    @Override
    protected void initMocks() {
        presenter = PowerMockito.mock(VideoTweetCreationPresenter.class);

        Shank.registerFactory(VideoTweetCreationPresenter.class, () -> presenter);
    }

    @Override
    protected void initActivity() {
        activity = Robolectric
                .buildActivity(VideoTweetCreationActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void when_cameraIsStarted_then_returnWrappedCamera() throws Exception {
        final VideoRecorder.CameraWrapper cameraWrapper = activity.startCamera();

        assertNotNull(cameraWrapper.getCamera());
    }

    @Test
    public void when_activityIsStopped_then_triggerPauseSubject() throws Exception {
        final AtomicInteger callCounter = new AtomicInteger();

        activity.whenViewPaused()
                .subscribe(s -> {
                    callCounter.incrementAndGet();
                });
        activity.onPause();

        assertEquals(1, callCounter.get());
    }

    @Test
    public void when_activityIsStarted_then_triggerStartSubject() throws Exception {
        final AtomicInteger callCounter = new AtomicInteger();

        activity.whenViewStarted()
                .subscribe(s -> {
                    callCounter.incrementAndGet();
                });
        activity.onStart();

        assertEquals(1, callCounter.get());
    }

    @Test
    public void when__then_() throws Exception {
        final ShadowActivity shadowActivity = Shadows.shadowOf(activity);

        activity.showTweetInTimeline(tweet);

        final Tweet result = TweetCreationHelper.getResult(shadowActivity.getResultIntent());

        assertEquals(tweet, result);
        assertEquals(Activity.RESULT_OK, shadowActivity.getResultCode());
        assertTrue(shadowActivity.isFinishing());
    }
}