Feature: Creating a SmallDomain

  Scenario: I create a random SmallDomain
    Given the application is ready
    When I create a random SmallDomain redirecting to google.com
    Then that random SmallDomain (redirecting to google.com) should exist
      And my response code should be 201

  Scenario: I try to retrieve an existing (but expired) SmallDomain
    Given a SmallDomain of small (redirecting to google.com) exists but is expired
    When I try to retrieve a SmallDomain of small
    Then a SmallDomain of small (redirecting to google.com) should NOT exist
    And my response code should be 404
    And my response body should match the standard error response

  Scenario: I try to retrieve a non-existent SmallDomain
    Given the application is ready
      And a SmallDomain of small does not yet exist
    When I try to retrieve a SmallDomain of small
    Then a SmallDomain of small (redirecting to google.com) should NOT exist
      And my response code should be 404
      And my response body should match the standard error response