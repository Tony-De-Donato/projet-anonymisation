Feature: The anonymization of a file storage

  Scenario: Anonymize a file
    Given A configuration for file storage
    When I post two files "test.txt" and "dictTest.json" for anonymize
    Then I should get the file anonymized and his content should be "Lorem ipsum username@domain.com dolor sit amet"