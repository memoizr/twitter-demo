package com.memoizrlabs.jeeter.stream;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BaseActivity;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.tweetcreation.text.TweetCreationActivity;
import com.memoizrlabs.jeeter.tweetcreation.TweetCreationHelper;
import com.memoizrlabs.jeeter.util.ActivityStarter;
import com.memoizrlabs.jeeter.tweetcreation.video.VideoTweetCreationActivity;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public final class StreamActivity extends BaseActivity<StreamPresenter.View> implements
        StreamPresenter.View {

    private static final int TEXT_RESULT = 1;
    private static final int VIDEO_RESULT = 2;
    private static final int SCROLL_DELAY_MS = 600;

    private final StreamAdapter adapter = new StreamAdapter();

    private final PublishSubject<Tweet> textTweetCreatedSubject = PublishSubject.create();
    private final PublishSubject<Tweet> videoTweetCreatedSubject = PublishSubject.create();

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.activity_stream);
        setupRecyclerView();
        setupToolbar();

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setIcon(R.drawable.twitter_logo_white);
        }
    }

    @NonNull
    @Override
    protected BasePresenter<StreamPresenter.View> getPresenter() {
        return Shank.withBoundScope(StreamActivity.class, whenViewIsDestroyed)
                    .provide(StreamPresenter.class);
    }

    @NonNull
    @Override
    protected StreamPresenter.View getView() {
        return this;
    }

    @NonNull
    @Override
    public Observable<Void> whenRefresh() {
        return RxSwipeRefreshLayout.refreshes(swipeRefresh);
    }

    @NonNull
    @Override
    public Observable<Object> whenTweetCreationStarted() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_new_tweet);
        return RxView.clicks(fab);
    }

    @NonNull
    @Override
    public Observable<Object> whenVideoTweetCreationStarted() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_new_video_tweet);
        final Drawable drawable = fab.getDrawable();
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.textColorGrey));
        return RxView.clicks(fab);
    }

    @Override
    public void showTweets(@NonNull List<Tweet> tweets) {
        adapter.setTweetItems(tweets);
    }

    @Override
    public void insertTweet(@NonNull Tweet tweet) {
        adapter.addItem(tweet);
        recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(0), SCROLL_DELAY_MS);
    }

    @Override
    public void showRefreshComplete() {
        swipeRefresh.setRefreshing(false);
    }

    @NonNull
    @Override
    public Observable<Tweet> showTweetCreation() {
        ActivityStarter.with(this)
                       .forResult(TEXT_RESULT)
                       .start(TweetCreationActivity.class);
        return textTweetCreatedSubject;
    }

    @NonNull
    @Override
    public Observable<Tweet> showVideoTweetCreation() {
        ActivityStarter.with(this)
                       .forResult(VIDEO_RESULT)
                       .start(VideoTweetCreationActivity.class);
        return videoTweetCreatedSubject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final Tweet result = TweetCreationHelper.getResult(data);

            if (requestCode == TEXT_RESULT) {
                textTweetCreatedSubject.onNext(result);
            } else if (requestCode == VIDEO_RESULT) {
                videoTweetCreatedSubject.onNext(result);
            }
        }
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.generic_error, Toast.LENGTH_LONG)
             .show();
    }
}
