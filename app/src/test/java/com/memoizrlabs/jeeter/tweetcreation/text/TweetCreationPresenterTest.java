package com.memoizrlabs.jeeter.tweetcreation.text;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BasePresenterTest;
import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.tweetcreation.StringValidityState;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Tweet.class)
public final class TweetCreationPresenterTest extends BasePresenterTest<TweetCreationPresenter,
        TweetCreationPresenter.View> {

    private static final String POST_CONTENT = "Hello world";
    private static final Scheduler UI_SCHEDULER = Schedulers.immediate();
    private static final int MAX_TWEET_LENTGTH_CHARACTERS = 140;

    private final PublishSubject<String> contentChangedSubject = PublishSubject.create();
    private final PublishSubject<Object> createActionSubject = PublishSubject.create();
    private final PublishSubject<Tweet> tweetCreatedSubject = PublishSubject.create();

    @Mock private TweetService tweetService;
    @Mock private Tweet tweet;

    @Override
    protected void setupMocks() {
        when(view.whenContentChanged()).thenReturn(contentChangedSubject);
        when(view.whenCreateAction()).thenReturn(createActionSubject);
        when(tweetService.postUpdate(anyString())).thenReturn(tweetCreatedSubject);
    }

    @NonNull
    @Override
    protected TweetCreationPresenter createPresenter() {
        return new TweetCreationPresenter(tweetService, new TweetContentValidator(), UI_SCHEDULER);
    }

    @NonNull
    @Override
    protected TweetCreationPresenter.View createView() {
        return mock(TweetCreationPresenter.View.class);
    }

    @Test
    public void when_createActionHappens_with_validContent_then_tweetIsCreated() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext(POST_CONTENT);
        createActionSubject.onNext(null);

        verify(tweetService).postUpdate(POST_CONTENT);
    }

    @Test
    public void when_createActionHappens_with_tooShortContent_then_tweetIsNotCreated() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext("");
        createActionSubject.onNext(null);

        verify(tweetService, never()).postUpdate(anyString());
    }

    @Test
    public void when_createActionHappens_with_tooShortContent_then_errorMessageIsShown() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext("");
        createActionSubject.onNext(null);

        verify(view).showError(StringValidityState.TOO_SHORT);
    }

    @Test
    public void when_createActionHappens_with_tooLongContent_then_postIsNotCreated() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext(buildLongString());
        createActionSubject.onNext(null);

        verify(tweetService, never()).postUpdate(anyString());
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
    public void when_createActionHappens_with_tooLongContent_then_errorMessageIsShown() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext(buildLongString());
        createActionSubject.onNext(null);

        verify(view).showError(StringValidityState.TOO_LONG);
    }

    @Test
    public void when_tweetCreationRequestFails_then_showError() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext(POST_CONTENT);
        createActionSubject.onNext(null);
        tweetCreatedSubject.onError(new Throwable());

        verify(view).showError();
    }

    @Test
    public void when_tweetCreationRequestSucceeds_then_showTweet() throws Exception {
        onViewAttached();

        contentChangedSubject.onNext(POST_CONTENT);
        createActionSubject.onNext(null);
        tweetCreatedSubject.onNext(tweet);

        verify(view).showTweet(any(Tweet.class));
    }
}