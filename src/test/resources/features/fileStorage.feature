Feature: The anonymization of a file storage

  Scenario: Anonymize a file
    Given A configuration for file storage
    When I post a file "text" for anonymized
    Then I should get the "file" text anonymized