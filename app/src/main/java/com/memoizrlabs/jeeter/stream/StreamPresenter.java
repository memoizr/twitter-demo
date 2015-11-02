package com.memoizrlabs.jeeter.stream;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.ErrorView;
import com.memoizrlabs.jeeter.common.PresenterView;
import com.memoizrlabs.jeeter.data.TweetRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;

final class StreamPresenter extends BasePresenter<StreamPresenter.View> {

    private static final int TIMELINE_ITEM_COUNT = 20;

    private final TweetRepository tweetRepository;
    private final Scheduler uiScheduler;

    private final AtomicReference<List<Tweet>> cachedTweets = new AtomicReference<>();

    StreamPresenter(@NonNull TweetRepository tweetRepository, @NonNull Scheduler uiScheduler) {
        this.tweetRepository = tweetRepository;
        this.uiScheduler = uiScheduler;
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        final Observable<List<Tweet>> emitCachedTweetOrNew =
                Observable.just(cachedTweets.get())
                          .flatMap(getCachedTweetsOrFetchNewIfNullCache());

        final Observable<List<Tweet>> freshTweets =
                view.whenRefresh()
                    .flatMap(x -> request())
                    .doOnNext(x -> view.showRefreshComplete());

        unsubOnViewDetach(
                getTweets(view, emitCachedTweetOrNew, freshTweets),
                createNewTweet(view),
                createNewVideoTweet(view)
        );
    }

    @NonNull
    private Func1<List<Tweet>, Observable<List<Tweet>>> getCachedTweetsOrFetchNewIfNullCache() {
        return reference -> {
            if (reference == null) {
                return request();
            } else {
                return Observable.just(reference);
            }
        };
    }

    @NonNull
    private Observable<List<Tweet>> request() {
        return tweetRepository
                .getTweets(TIMELINE_ITEM_COUNT)
                .observeOn(uiScheduler)
                .doOnNext(cachedTweets::set);
    }

    @NonNull
    private static Subscription getTweets(@NonNull View view,
                                          @NonNull Observable<List<Tweet>> cachedTweetOrNew,
                                          @NonNull Observable<List<Tweet>> freshTweets) {
        return Observable.merge(cachedTweetOrNew, freshTweets)
                         .subscribe(view::showTweets);
    }

    @NonNull
    private static Subscription createNewTweet(@NonNull View view) {
        return view.whenTweetCreationStarted()
                   .flatMap(event -> view.showTweetCreation())
                   .subscribe(view::insertTweet);
    }

    @NonNull
    private static Subscription createNewVideoTweet(@NonNull View view) {
        return view.whenVideoTweetCreationStarted()
                   .flatMap(event -> view.showVideoTweetCreation())
                   .subscribe(view::insertTweet);
    }

    interface View extends PresenterView, ErrorView {

        @NonNull
        Observable<Void> whenRefresh();
        @NonNull
        Observable<Object> whenTweetCreationStarted();
        @NonNull
        Observable<Object> whenVideoTweetCreationStarted();
        @NonNull
        Observable<Tweet> showTweetCreation();
        @NonNull
        Observable<Tweet> showVideoTweetCreation();

        void showTweets(@NonNull List<Tweet> tweets);
        void insertTweet(@NonNull Tweet tweet);
        void showRefreshComplete();
    }
}
