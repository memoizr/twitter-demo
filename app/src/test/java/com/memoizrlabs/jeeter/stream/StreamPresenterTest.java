package com.memoizrlabs.jeeter.stream;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.BasePresenterTest;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.api.model.User;
import com.memoizrlabs.jeeter.data.TweetRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({User.class, Tweet.class})
public class StreamPresenterTest extends BasePresenterTest<StreamPresenter, StreamPresenter.View> {

    private final Scheduler uiScheduler = Schedulers.immediate();

    private final PublishSubject<List<Tweet>> timelineSubject = PublishSubject.create();
    private final PublishSubject<Void> refreshSubject = PublishSubject.create();
    private final PublishSubject<Object> createNewTweetActionSubject = PublishSubject.create();
    private final PublishSubject<Object> createNewVideoTweetActionSubject = PublishSubject.create();
    private final PublishSubject<Tweet> tweetCreatedSubject = PublishSubject.create();
    private final PublishSubject<Tweet> videoTweetCreatedSubject = PublishSubject.create();

    @Mock private TweetRepository tweetRepository;
    @Mock private User user;
    @Mock private Tweet tweet;

    @NonNull
    @Override
    protected StreamPresenter createPresenter() {
        return new StreamPresenter(tweetRepository, uiScheduler);
    }

    @NonNull
    @Override
    protected StreamPresenter.View createView() {
        return mock(StreamPresenter.View.class);
    }

    @Override
    protected void setupMocks() {
        when(tweetRepository.getTweets(anyInt())).thenReturn(timelineSubject.take(1));

        when(view.whenRefresh()).thenReturn(refreshSubject);
        when(view.whenTweetCreationStarted()).thenReturn(createNewTweetActionSubject);
        when(view.whenVideoTweetCreationStarted()).thenReturn(createNewVideoTweetActionSubject);
        when(view.showTweetCreation()).thenReturn(tweetCreatedSubject);
        when(view.showVideoTweetCreation()).thenReturn(videoTweetCreatedSubject);
    }

    @Test
    public void when_viewIsAttached_fetchesTweets() {
        onViewAttached();

        returnTweetsFromRepository();

        verify(tweetRepository).getTweets(anyInt());
        verify(view).showTweets(anyListOf(Tweet.class));
    }

    private void returnTweetsFromRepository() {
        timelineSubject.onNext(Collections.singletonList(tweet));
    }

    @Test
    public void when_viewIsAttached_thenDetached_thenReAttached_fetchesTweetsOnce() {
        onViewAttached();

        returnTweetsFromRepository();

        onViewDetached();
        onViewAttached();
        returnTweetsFromRepository();

        verify(tweetRepository).getTweets(anyInt());
    }

    @Test
    public void when_refreshHappens_fetchesTweets() {
        onViewAttached();

        returnTweetsFromRepository();

        when(tweetRepository.getTweets(anyInt())).thenReturn(timelineSubject.take(1));

        refreshStream();

        verify(view, times(2)).showTweets(anyListOf(Tweet.class));
        verify(tweetRepository, times(2)).getTweets(anyInt());
    }

    private void refreshStream() {
        refreshSubject.onNext(null);
        returnTweetsFromRepository();
    }

    @Test
    public void when_refreshCompletes_disableRefreshing() {
        onViewAttached();

        refreshStream();

        verify(view).showRefreshComplete();
    }

    @Test
    public void when_createTweetIsSelected_then_showCreateTweet() throws Exception {
        onViewAttached();

        triggerCreateNewTweetAction();

        verify(view).showTweetCreation();
    }

    private void triggerCreateNewTweetAction() {
        createNewTweetActionSubject.onNext(null);
    }

    @Test
    public void when_tweetIsCreated_then_showLatestTweet() throws Exception {
        onViewAttached();

        triggerCreateNewTweetAction();
        tweetCreatedSubject.onNext(tweet);

        verify(view).insertTweet(tweet);
    }

    @Test
    public void when_createVideoTweetIsSelected_then_showCreateVideoTweet() throws Exception {
        onViewAttached();

        triggerCreateNewVideoTweetAction();

        verify(view).showVideoTweetCreation();
    }

    private void triggerCreateNewVideoTweetAction() {
        createNewVideoTweetActionSubject.onNext(null);
    }

    @Test
    public void when_videoTweetIsCreated_then_showLatestTweet() throws Exception {
        onViewAttached();

        triggerCreateNewVideoTweetAction();
        videoTweetCreatedSubject.onNext(tweet);

        verify(view).insertTweet(tweet);
    }
}
