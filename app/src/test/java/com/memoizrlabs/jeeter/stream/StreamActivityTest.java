package com.memoizrlabs.jeeter.stream;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.BaseRobolectricTest;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.api.model.TweetEntities;
import com.memoizrlabs.jeeter.api.model.User;
import com.memoizrlabs.jeeter.tweetcreation.text.TweetCreationActivity;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.Shadows.shadowOf;

@PrepareForTest({Tweet.class, User.class, StreamPresenter.class, TweetEntities.class})
public class StreamActivityTest extends BaseRobolectricTest {

    private Tweet tweet;
    private User user;
    private StreamPresenter presenter;
    private TweetEntities entities;

    private StreamActivity activity;

    protected void initMocks() {
        tweet = PowerMockito.mock(Tweet.class);
        user = PowerMockito.mock(User.class);
        presenter = PowerMockito.mock(StreamPresenter.class);
        entities = PowerMockito.mock(TweetEntities.class);

        Shank.registerFactory(StreamPresenter.class, () -> presenter);
        when(tweet.getUser()).thenReturn(user);
        when(tweet.getEntities()).thenReturn(entities);
    }

    protected void initActivity() {
        activity = Robolectric
                .buildActivity(StreamActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void when_newPostIsShown_then_startTwitterCreationActivity() throws Exception {
        final Intent expectedIntent = new Intent(activity, TweetCreationActivity.class);

        activity.showTweetCreation();

        assertEquals(expectedIntent, shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void when_tweetIsCreatedSuccessfully_then_emitTweet() throws Exception {
        final AtomicInteger callCounter = new AtomicInteger();

        activity.showTweetCreation().subscribe(returnedTweet -> {
            assertEquals(tweet, returnedTweet);
            callCounter.incrementAndGet();
        });

        shadowOf(activity).receiveResult(
                new Intent(activity, TweetCreationActivity.class),
                Activity.RESULT_OK,
                new Intent().putExtra("tweet", tweet));

        assertEquals(1, callCounter.get());
    }

    @Test
    public void when_showTweets_then_displayThemInRecyclerView() throws Exception {
        final RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        final String mockDate = "Wed Dec 01 17:08:03 +0000 2010";
        when(tweet.getCreatedAt()).thenReturn(mockDate);

        activity.showTweets(Arrays.asList(tweet, tweet));

        triggerRecyclerViewLayout(recyclerView);

        final int childCount = recyclerView.getChildCount();

        assertEquals(2, childCount);
    }

    private static void triggerRecyclerViewLayout(RecyclerView recyclerView) {
        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);
    }

    @Test
    public void when_tweetsAreShown_then_displayTweetDetails() throws Exception {
        final RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);

        final String mockDate = "Wed Dec 01 17:08:03 +0000 2010";
        final String mockText = "mockText";
        final String mockUsername = "mockUsername";

        when(tweet.getCreatedAt()).thenReturn(mockDate);
        when(tweet.getText()).thenReturn(mockText);
        when(user.getName()).thenReturn(mockUsername);

        activity.showTweets(Collections.singletonList(tweet));

        triggerRecyclerViewLayout(recyclerView);

        final View itemView = recyclerView.getChildAt(0);

        final TextView date = (TextView) itemView.findViewById(R.id.textview_date);
        final TextView username = (TextView) itemView.findViewById(R.id.textview_username);
        final TextView text = (TextView) itemView.findViewById(R.id.textinputlayout_tweet_content);

        assertEquals("1 Dec 2010", date.getText());
        assertEquals(mockUsername, username.getText());
        assertEquals(mockText, text.getText());
    }
}
