package com.memoizrlabs.jeeter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseRobolectricTest {

    @Rule public final PowerMockRule rule = new PowerMockRule();

    @Before
    public void before() {
        initMocks();
        initActivity();
    }

    abstract protected void initMocks();

    abstract protected void initActivity();
}
