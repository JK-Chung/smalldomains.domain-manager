Feature: Creating a SmallDomain

  Scenario: I try to create a SmallDomain which does not yet exist
    Given the application is ready
      And a SmallDomain of small does not yet exist
    When my request is to create a SmallDomain of small (redirecting to google.com)
    Then a SmallDomain of small (redirecting to google.com) should be created
      And my response code should be 201
      And my response body should be the SmallDomain of small (redirecting to google.com)