package com.memoizrlabs.jeeter;

import android.app.Application;

import com.memoizrlabs.Shank;
import com.memoizrlabs.ShankModuleRegister;
import com.memoizrlabs.jeeter.api.ServiceModule;
import com.memoizrlabs.jeeter.data.DataModule;
import com.memoizrlabs.jeeter.splash.SplashModule;
import com.memoizrlabs.jeeter.login.LoginModule;
import com.memoizrlabs.jeeter.session.SessionHandler;
import com.memoizrlabs.jeeter.stream.StreamModule;
import com.memoizrlabs.jeeter.tweetcreation.text.TweetCreationModule;
import com.memoizrlabs.jeeter.util.FileUtil;
import com.memoizrlabs.jeeter.tweetcreation.video.VideoTweetCreationModule;

public class JeeterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        registerShankModules();
        initializeTwitterSession();
        installFfmpeg();
    }

    protected void registerShankModules() {
        ShankModuleRegister.registerModules(
                new AppModule(),
                new ServiceModule(),
                new DataModule(),
                new SplashModule(),
                new LoginModule(),
                new StreamModule(),
                new VideoTweetCreationModule(getCacheDir().toString()),
                new TweetCreationModule()
        );
    }

    protected void initializeTwitterSession() {
        final SessionHandler sessionHandler = Shank.provide(SessionHandler.class);
        sessionHandler.createSession(this);
    }

    protected void installFfmpeg() {
        FileUtil.installFfmpeg(getApplicationContext(), getCacheDir().toString());
    }
}
