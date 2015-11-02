package com.memoizrlabs.jeeter;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/features", plugin = {"pretty"})
public class RunCucumber {

}
