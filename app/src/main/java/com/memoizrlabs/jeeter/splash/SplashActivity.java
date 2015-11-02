package com.memoizrlabs.jeeter.splash;

import android.support.annotation.NonNull;

import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.common.BaseActivity;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.login.LoginActivity;
import com.memoizrlabs.jeeter.stream.StreamActivity;
import com.memoizrlabs.jeeter.util.ActivityStarter;

public final class SplashActivity extends BaseActivity<SplashPresenter.View> implements SplashPresenter.View {

    @NonNull
    @Override
    protected SplashPresenter.View getView() {
        return this;
    }

    @NonNull
    @Override
    protected BasePresenter<SplashPresenter.View> getPresenter() {
        return Shank.withBoundScope(SplashActivity.class, whenViewIsDestroyed).provide(SplashPresenter.class);
    }

    @Override
    public void showTweets() {
        ActivityStarter.with(this).start(StreamActivity.class);
        finish();
    }

    public void showLogin() {
        ActivityStarter.with(this).start(LoginActivity.class);
        finish();
    }
}
