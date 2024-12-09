Feature: the search of a regex rule

  Scenario: Search a regex rule
    When I search the regex rule by name like "test"
    Then I should get a regex rule named like "test"

  Scenario: Search a regex rule by file name
    When I search the regex rule by file name like "test"
    Then I should get a regex rule from my file name search

  Scenario: Get all regex rules
    When I get all regex rules
    Then I should get at least 2 different regex rules