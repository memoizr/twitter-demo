package com.memoizrlabs.jeeter.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.common.BaseActivity;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.stream.StreamActivity;
import com.memoizrlabs.jeeter.util.ActivityStarter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public final class LoginActivity extends BaseActivity<LoginPresenter.View> implements
        LoginPresenter.View {

    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.activity_login);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterloginbutton);
    }

    @NonNull
    @Override
    protected BasePresenter<LoginPresenter.View> getPresenter() {
        return Shank.withBoundScope(LoginActivity.class, whenViewIsDestroyed)
                    .provide(LoginPresenter.class);
    }

    @NonNull
    @Override
    protected LoginPresenter.View getView() {
        return this;
    }

    @Override
    public void showTimeline() {
        ActivityStarter.with(this)
                       .start(StreamActivity.class);
        finish();
    }

    @Override
    public void setCallback(@NonNull Callback<TwitterSession> callback) {
        twitterLoginButton.setCallback(callback);
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.generic_error, Toast.LENGTH_LONG)
             .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
