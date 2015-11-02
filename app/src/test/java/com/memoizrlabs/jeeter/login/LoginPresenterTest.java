package com.memoizrlabs.jeeter.login;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BasePresenterTest;
import com.memoizrlabs.jeeter.api.RxCallback;
import com.memoizrlabs.jeeter.session.SessionHandler;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.junit.Test;
import org.mockito.Mock;

import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class LoginPresenterTest extends BasePresenterTest<LoginPresenter, LoginPresenter.View> {

    @Mock private SessionHandler sessionHandler;
    @Mock private Tweet tweet;
    @Mock private TwitterSession twitterSession;
    @Mock private Result<TwitterSession> twitterResult;
    @Mock private RxCallback<TwitterSession> callback;

    private PublishSubject<Result<TwitterSession>> resultSubject = PublishSubject.create();

    @Override
    protected void setupMocks() {
        when(callback.onResult()).thenReturn(resultSubject);
    }

    @NonNull
    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(sessionHandler, callback);
    }

    @NonNull
    @Override
    protected LoginPresenter.View createView() {
        return mock(LoginPresenter.View.class);
    }

    @Test
    public void when_loginSucceeds_then_showTimeline() throws Exception {
        onViewAttached();

        resultSubject.onNext(twitterResult);

        verify(view).showTimeline();
    }

    @Test
    public void when_loginSucceeds_then_setSession() throws Exception {
        onViewAttached();

        resultSubject.onNext(twitterResult);

        verify(sessionHandler).performLogin(any(TwitterSession.class));
    }

    @Test
    public void when_viewIsAttached_then_setCallback() throws Exception {
        onViewAttached();

        verify(view).setCallback(callback);
    }

    @Test
    public void when_loginFails_then_showError() throws Exception {
        onViewAttached();

        resultSubject.onError(new Throwable());
        resultSubject = PublishSubject.create();

        verify(view).showError();
    }

    @Test
    public void when_loginFails_then_resetCallback() throws Exception {
        onViewAttached();

        resultSubject.onError(new Throwable());

        verify(callback).reset();
    }

    @Test
    public void when_loginFails_then_recoversGracefully() throws Exception {
        final PublishSubject<Result<TwitterSession>> newResultSubject = PublishSubject.create();
        when(callback.reset()).thenReturn(newResultSubject);

        onViewAttached();

        resultSubject.onError(new Throwable());

        verify(view).showError();

        newResultSubject.onNext(twitterResult);

        verify(view).showTimeline();
    }
}