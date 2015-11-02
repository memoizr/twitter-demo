package com.memoizrlabs.jeeter.splash;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BasePresenterTest;
import com.memoizrlabs.jeeter.session.SessionHandler;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplashPresenterTest extends BasePresenterTest<SplashPresenter, SplashPresenter.View> {

    @Mock
    private SessionHandler sessionHandler;

    @NonNull
    @Override
    protected SplashPresenter createPresenter() {
        return new SplashPresenter(sessionHandler);
    }

    @NonNull
    @Override
    protected SplashPresenter.View createView() {
        return mock(SplashPresenter.View.class);
    }

    @Test
    public void when_viewIsAttached_then_checkIfLoggedIn() {
        onViewAttached();
        verify(sessionHandler).isLoggedIn();
    }

    @Test public void when_userIsNotLoggedIn_then_showLogin() {
        when(sessionHandler.isLoggedIn()).thenReturn(false);
        onViewAttached();

        verify(view).showLogin();
    }

    @Test public void when_userIsLoggedIn_then_showTweets() {
        when(sessionHandler.isLoggedIn()).thenReturn(true);
        onViewAttached();

        verify(view).showTweets();
    }
}