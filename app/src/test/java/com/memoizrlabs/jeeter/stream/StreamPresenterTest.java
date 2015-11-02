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
    private final PublishSubject<Object> whenCreateNewTweetSubject = PublishSubject.create();
    private final PublishSubject<Object> whenCreateNewVideoTweetSubject = PublishSubject.create();
    private final PublishSubject<Tweet> whenTweetCreatedSubject = PublishSubject.create();
    private final PublishSubject<Tweet> whenVideoTweetCreatedSubject = PublishSubject.create();

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
        when(view.whenTweetCreationStarted()).thenReturn(whenCreateNewTweetSubject);
        when(view.whenVideoTweetCreationStarted()).thenReturn(whenCreateNewVideoTweetSubject);
        when(view.showTweetCreation()).thenReturn(whenTweetCreatedSubject);
        when(view.showVideoTweetCreation()).thenReturn(whenVideoTweetCreatedSubject);
    }

    @Test
    public void when_viewIsAttached_fetchesTweets() {
        onViewAttached();
        timelineSubject.onNext(Collections.singletonList(tweet));

        verify(tweetRepository).getTweets(anyInt());
        verify(view).showTweets(anyListOf(Tweet.class));
    }

    @Test
    public void when_viewIsAttached_thenDetached_thenReAttached_fetchesTweetsOnce() {
        onViewAttached();
        timelineSubject.onNext(Collections.singletonList(tweet));

        onViewDetached();
        onViewAttached();
        timelineSubject.onNext(Collections.singletonList(tweet));

        verify(tweetRepository).getTweets(anyInt());
    }

    @Test
    public void when_refreshHappens_fetchesTweets() {
        onViewAttached();

        timelineSubject.onNext(Collections.singletonList(tweet));
        when(tweetRepository.getTweets(anyInt())).thenReturn(timelineSubject.take(1));

        refreshStream();

        verify(view, times(2)).showTweets(anyListOf(Tweet.class));
        verify(tweetRepository, times(2)).getTweets(anyInt());
    }

    private void refreshStream() {
        refreshSubject.onNext(null);
        timelineSubject.onNext(Collections.singletonList(tweet));
    }

    @Test
    public void when_refreshCompletes_disableRefreshing() {
        onViewAttached();

        refreshStream();

        verify(view).showRefreshComplete();
    }

    @Test
    public void when_createPostIsSelected_then_showCreatePost() throws Exception {
        onViewAttached();

        whenCreateNewTweetSubject.onNext(null);

        verify(view).showTweetCreation();
    }

    @Test
    public void when_postIsCreated_then_showLatestPost() throws Exception {
        onViewAttached();

        whenCreateNewTweetSubject.onNext(null);
        whenTweetCreatedSubject.onNext(tweet);

        verify(view).insertTweet(tweet);
    }

    @Test
    public void when_createVideoTweetIsSelected_then_showCreateVideoTweet() throws Exception {
        onViewAttached();

        whenCreateNewVideoTweetSubject.onNext(null);

        verify(view).showVideoTweetCreation();
    }
}
