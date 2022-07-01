Feature: Creating a SmallDomain

  Scenario: I create a random SmallDomain
    Given the application is ready
    When I create a random SmallDomain redirecting to google.com
    Then that random SmallDomain (redirecting to https://google.com) should exist
      And my response code should be 201

  Scenario: I try to retrieve an existing (but expired) SmallDomain
    Given a SmallDomain of small (redirecting to google.com) exists but is expired
    When I try to retrieve a SmallDomain of small
    Then a SmallDomain of small (redirecting to https://google.com) should NOT exist
    And my response code should be 404
    And my response body should match the standard error response

  Scenario: I try to retrieve a non-existent SmallDomain
    Given the application is ready
      And a SmallDomain of small does not yet exist
    When I try to retrieve a SmallDomain of small
    Then a SmallDomain of small (redirecting to https://google.com) should NOT exist
      And my response code should be 404
      And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with an empty large domain
    Given the application is ready
    When I try to create a SmallDomain with a blank large domain
    Then my response code should be 400
    And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with a null large domain
    Given the application is ready
    When I create a random SmallDomain with a null large domain
    Then my response code should be 400
    And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with no TLD
    Given the application is ready
    When I create a random SmallDomain redirecting to googlecom
    Then my response code should be 400
      And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with the FTP scheme
    Given the application is ready
    When I create a random SmallDomain redirecting to ftp://google.com
    Then my response code should be 400
    And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with only a TLD and no domain
    Given the application is ready
    When I create a random SmallDomain redirecting to .com
    Then my response code should be 400
    And my response body should match the standard error response

  Scenario: I try to create a SmallDomain with only a domain and no TLD
    Given the application is ready
    When I create a random SmallDomain redirecting to com.
    Then my response code should be 400
    And my response body should match the standard error response