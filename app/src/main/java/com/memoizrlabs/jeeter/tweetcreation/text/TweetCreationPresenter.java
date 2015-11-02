package com.memoizrlabs.jeeter.tweetcreation.text;

import android.support.annotation.NonNull;

import com.memoizrlabs.jeeter.api.TweetService;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.ErrorView;
import com.memoizrlabs.jeeter.common.PresenterView;
import com.memoizrlabs.jeeter.tweetcreation.TweetContentValidator;
import com.memoizrlabs.jeeter.util.RxUtils;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

final class TweetCreationPresenter extends BasePresenter<TweetCreationPresenter.View> {

    private final TweetService tweetService;
    private final TweetContentValidator tweetContentValidator;
    private final Scheduler uiScheduler;

    private final BehaviorSubject<String> contentSubject = BehaviorSubject.create("");

    TweetCreationPresenter(@NonNull TweetService tweetService,
                           @NonNull TweetContentValidator tweetContentValidator,
                           @NonNull Scheduler uiScheduler) {
        this.tweetService = tweetService;
        this.tweetContentValidator = tweetContentValidator;
        this.uiScheduler = uiScheduler;
    }

    @Override
    public void onViewAttached(@NonNull View view) {
        super.onViewAttached(view);

        unsubOnViewDetach(
                cacheLatestContent(view),
                createPost(view)
        );
    }

    @NonNull
    private Subscription cacheLatestContent(@NonNull View view) {
        return view.whenContentChanged()
                   .subscribe(contentSubject::onNext);
    }

    @NonNull
    private Subscription createPost(@NonNull View view) {
        return view.whenCreateAction()
                   .map(v -> contentSubject.getValue())
                   .flatMap(content -> tweetContentValidator.filterValidAndShowErrorForInvalid(content, view))
                   .flatMap(content -> tweetService.postUpdate(content)
                                                   .compose(RxUtils.handleError(view, uiScheduler)))
                   .subscribe(view::showTweet);
    }

    interface View extends PresenterView, ErrorView, ErrorView.Validatable {

        @NonNull
        Observable<String> whenContentChanged();
        @NonNull
        Observable<Object> whenCreateAction();

        void showTweet(@NonNull Tweet tweet);
    }
}
