Feature: Creating a SmallDomain

  Scenario: I try to create a SmallDomain which does not yet exist
    Given the application is ready
      And a SmallDomain of small does not yet exist
    When I try to create a SmallDomain of small (redirecting to google.com)
    Then a SmallDomain of small (redirecting to google.com) should exist
      And my response code should be 201
      And my response body should be the SmallDomain of small (redirecting to google.com)

  Scenario: I try to create a duplicate SmallDomain
    Given the application is ready
      And there already exists a SmallDomain of small (redirecting to google.com)
    When I try to create a SmallDomain of small (redirecting to duplicate.com)
    Then a SmallDomain of small (redirecting to google.com) should exist
      And a SmallDomain of small (redirecting to duplicate.com) should NOT exist
      And my response code should be 409
      And my response body should match the standard error response

  Scenario: I try to create a SmallDomain which has a bad SmallDomain
    Given the application is ready
    When I try to create a SmallDomain of small@~: (redirecting to google.com)
    Then a SmallDomain of small@~: (redirecting to google.com) should NOT exist
      And my response code should be 400
      And my response body should match the standard error response

  Scenario: I try to retrieve a non-existent SmallDomain
    Given the application is ready
      And a SmallDomain of small does not yet exist
    When I try to retrieve a SmallDomain of small
    Then a SmallDomain of small@~: (redirecting to google.com) should NOT exist
      And my response code should be 404
      And my response body should match the standard error response