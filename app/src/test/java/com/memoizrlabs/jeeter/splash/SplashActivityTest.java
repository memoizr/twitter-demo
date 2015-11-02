package com.memoizrlabs.jeeter.splash;

import android.content.Intent;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.BaseRobolectricTest;
import com.memoizrlabs.jeeter.login.LoginActivity;
import com.memoizrlabs.jeeter.stream.StreamActivity;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;

import static junit.framework.TestCase.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.robolectric.Shadows.shadowOf;

@PrepareForTest(SplashPresenter.class)
public class SplashActivityTest extends BaseRobolectricTest {

    private SplashPresenter presenter;
    private SplashActivity activity;

    @Override
    protected void initMocks() {
        presenter = mock(SplashPresenter.class);

        Shank.registerFactory(SplashPresenter.class, () -> presenter);
    }

    @Override
    protected void initActivity() {
        activity = Robolectric
                .buildActivity(SplashActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void when_tweetsAreShown_then_showTweetActivity() throws Exception {
        final Intent expectedIntent = new Intent(activity, StreamActivity.class);

        activity.showTweets();

        assertEquals(expectedIntent, shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void when_loginIsShown_then_showLoginActivity() throws Exception {
        final Intent expectedIntent = new Intent(activity, LoginActivity.class);

        activity.showLogin();

        assertEquals(expectedIntent, shadowOf(activity).getNextStartedActivity());
    }
}
