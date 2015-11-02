package com.memoizrlabs.jeeter;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

public class TestJeeterApplication extends JeeterApplication implements TestLifecycleApplication {

    @Override
    public void onCreate() {
    }

    @Override
    public void beforeTest(Method method) {
    }

    @Override
    public void prepareTest(Object test) {
    }

    @Override
    public void afterTest(Method method) {
    }
}
