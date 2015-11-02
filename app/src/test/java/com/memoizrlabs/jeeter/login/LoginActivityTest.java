package com.memoizrlabs.jeeter.login;

import android.content.Intent;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.BaseRobolectricTest;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.stream.StreamActivity;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;

import static junit.framework.TestCase.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.robolectric.Shadows.shadowOf;

@PrepareForTest(LoginPresenter.class)
public class LoginActivityTest extends BaseRobolectricTest {

    private LoginPresenter presenter;
    private LoginActivity activity;

    @Override
    protected void initMocks() {
        presenter = mock(LoginPresenter.class);

        Shank.registerFactory(LoginPresenter.class, () -> presenter);
    }

    @Override
    protected void initActivity() {
        activity = Robolectric
                .buildActivity(LoginActivity.class)
                .get();
    }

    @Test
    public void when_tweetsAreShown_then_showTweetActivity() throws Exception {
        final Intent expectedIntent = new Intent(activity, StreamActivity.class);

        activity.showTimeline();

        assertEquals(expectedIntent, shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void when_loginFails_then_showError() throws Exception {
        final String errorMessage = RuntimeEnvironment.application.getResources().getString(R.string.generic_error);

        activity.showError();

        assertEquals(errorMessage, ShadowToast.getTextOfLatestToast());
    }
}
