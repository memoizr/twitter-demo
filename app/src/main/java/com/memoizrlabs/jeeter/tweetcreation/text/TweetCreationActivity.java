package com.memoizrlabs.jeeter.tweetcreation.text;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BaseActivity;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.Validator;
import com.memoizrlabs.jeeter.tweetcreation.TweetCreationHelper;
import com.memoizrlabs.jeeter.util.RxEditText;

import rx.Observable;

public final class TweetCreationActivity extends BaseActivity<TweetCreationPresenter.View> implements
        TweetCreationPresenter.View {

    private EditText contentEditText;
    private View createTweetView;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_tweet_creation);

        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_tweet_content);
        contentEditText = textInputLayout.getEditText();
        createTweetView = findViewById(R.id.view_create_tweet);

        setupToolbar();
    }

    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @NonNull
    @Override
    protected BasePresenter<TweetCreationPresenter.View> getPresenter() {
        return Shank.withBoundScope(TweetCreationActivity.class, whenViewIsDestroyed)
                    .provide(TweetCreationPresenter.class);
    }

    @NonNull
    @Override
    protected TweetCreationPresenter.View getView() {
        return this;
    }

    @NonNull
    @Override
    public Observable<String> whenContentChanged() {
        return RxEditText.textChanges(contentEditText);
    }

    @NonNull
    @Override
    public Observable<Object> whenCreateAction() {
        return RxView.clicks(createTweetView);
    }

    @Override
    public void showTweet(@NonNull Tweet tweet) {
        final Intent intent = new Intent();
        intent.putExtra(TweetCreationHelper.TWEET, tweet);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.generic_error, Toast.LENGTH_LONG)
             .show();
    }

    @Override
    public void showError(@NonNull Validator.ValidityState invalidState) {
        Toast.makeText(this, invalidState.getMessage(), Toast.LENGTH_LONG)
             .show();
    }
}
