package com.memoizrlabs.jeeter;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class BaseMockitoTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
}
