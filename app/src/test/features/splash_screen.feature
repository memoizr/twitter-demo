Feature: SplashScreen

  Scenario: Redirects to login if the user is not logged in
    Given I am on the splash screen
    When I am not logged in
    Then I am taken to the login screen

  Scenario: Redirects to tweets if the user is logged in
    Given I am on the splash screen
    When I am logged in
    Then I am taken to the stream screen
