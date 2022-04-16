Feature: Guess the word

  Scenario: A tagged scenario
    Given I tag a scenario
    When I select tests with that tag for execution
    Then my tagged scenario is executed

  Scenario: A tagged scenario 2
    Given I tag a scenario
    When I select tests with that tag for execution
    Then my tagged scenario is executed