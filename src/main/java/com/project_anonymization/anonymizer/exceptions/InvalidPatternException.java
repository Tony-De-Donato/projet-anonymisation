package com.project_anonymization.anonymizer.exceptions;

public class InvalidPatternException extends AnonymizerServiceException {
    public InvalidPatternException(String message, String pattern, int index) {
        super(message + " Pattern: " + pattern + " at index: " + index);
    }
}