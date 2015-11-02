package com.memoizrlabs.jeeter.api;

import com.memoizrlabs.jeeter.BaseMockitoTest;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;

public class RxCallbackTest extends BaseMockitoTest {

    public static final String DATA = "data";

    private Result<String> twitterResult = new Result<>(DATA, null);

    private RxCallback<String> callback;

    @Override
    public void setup() {
        super.setup();

        callback = new RxCallback<>();
    }

    @Test
    public void when_callbackSucceeds_then_fireOnNext() throws Exception {
        final AtomicInteger callCounter = new AtomicInteger();

        callback.onResult()
                .subscribe(result -> {
                    callCounter.incrementAndGet();
                    assertEquals(DATA, result.data);
                });

        callback.success(twitterResult);

        assertEquals(1, callCounter.get());
    }

    @Test
    public void when_callbackFails_then_fireOnNext() throws Exception {
        final TwitterException throwable = new TwitterException("oops");
        final AtomicInteger callCounter = new AtomicInteger();
        final AtomicInteger errorCounter = new AtomicInteger();

        callback.onResult()
                .subscribe(result -> {
                               callCounter.incrementAndGet();
                           },
                           e -> {
                               errorCounter.incrementAndGet();
                               assertEquals(e, throwable);
                           });

        callback.failure(throwable);

        assertEquals(0, callCounter.get());
        assertEquals(1, errorCounter.get());
    }

    @Test
    public void when_callbackFails_and_notReset_then_doNotFireSuccessiveEvents() throws Exception {
        final TwitterException throwable = new TwitterException("oops");
        final AtomicInteger callCounter = new AtomicInteger();

        callback.onResult()
                .subscribe(result -> {
                               callCounter.incrementAndGet();
                           },
                           e -> {
                               assertEquals(e, throwable);
                           });

        callback.failure(throwable);

        assertEquals(0, callCounter.get());

        callback.onResult()
                .subscribe(result -> {
                               callCounter.incrementAndGet();
                           },
                           e -> {
                               assertEquals(e, throwable);
                           });

        callback.success(twitterResult);

        assertEquals(0, callCounter.get());
    }

    @Test
    public void when_callbackFails_and_reset_then_doNotFireSuccessiveEvents() throws Exception {
        final TwitterException throwable = new TwitterException("oops");
        final AtomicInteger callCounter = new AtomicInteger();

        callback.onResult()
                .subscribe(result -> {
                               callCounter.incrementAndGet();
                           },
                           e -> {
                               assertEquals(e, throwable);
                           });

        callback.failure(throwable);

        assertEquals(0, callCounter.get());

        callback.reset();

        callback.onResult()
                .subscribe(result -> {
                               callCounter.incrementAndGet();
                           },
                           e -> {
                               assertEquals(e, throwable);
                           });

        callback.success(twitterResult);

        assertEquals(1, callCounter.get());
    }
}