package com.memoizrlabs.jeeter.splash;

import com.memoizrlabs.jeeter.session.SessionHandler;

import org.powermock.core.classloader.annotations.PrepareForTest;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(SessionHandler.class)
public class SplashSteps {

    private SessionHandler sessionHandler;
    private SplashPresenter splashPresenter;
    private SplashPresenter.View view;

    @Before
    public void before() {
        sessionHandler = mock(SessionHandler.class);
    }

    @Given("^I am on the splash screen$")
    public void i_am_on_the_splash_screen() throws Throwable {
        splashPresenter = new SplashPresenter(sessionHandler);
        view = mock(SplashPresenter.View.class);
    }

    @When("^I am not logged in$")
    public void i_am_not_logged_in() throws Throwable {
        when(sessionHandler.isLoggedIn()).thenReturn(false);
        splashPresenter.onViewAttached(view);
    }

    @Then("^I am taken to the login screen$")
    public void i_am_taken_to_the_login_screen() throws Throwable {
        verify(view).showLogin();
    }

    @When("^I am logged in$")
    public void i_am_logged_in() throws Throwable {
        when(sessionHandler.isLoggedIn()).thenReturn(true);
        splashPresenter.onViewAttached(view);
    }

    @Then("^I am taken to the stream screen$")
    public void i_am_taken_to_the_stream_screen() throws Throwable {
        verify(view).showTweets();
    }
}
