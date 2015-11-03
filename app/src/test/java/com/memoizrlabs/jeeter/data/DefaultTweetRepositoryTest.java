package com.memoizrlabs.jeeter.data;

import com.memoizrlabs.jeeter.BaseMockitoTest;
import com.memoizrlabs.jeeter.api.TweetService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

public class DefaultTweetRepositoryTest extends BaseMockitoTest {

    private static final int COUNT = 10;

    private TweetRepository tweetRepository;

    @Mock private TweetService tweetService;

    @Before
    @Override
    public void setup() {
        super.setup();
        tweetRepository = new DefaultTweetRepository(tweetService);
    }

    @Test
    public void when_getTweets_then_getTweets() throws Exception {
        tweetRepository.getTweets(COUNT);

        verify(tweetService).getMainTimeline(COUNT);
    }
}