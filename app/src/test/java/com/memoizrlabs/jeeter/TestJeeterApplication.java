package com.memoizrlabs.jeeter;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

/**
 * This class is instatiated as the application class for Robolectric tests.
 */
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
