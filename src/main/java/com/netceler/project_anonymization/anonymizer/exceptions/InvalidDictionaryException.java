package com.netceler.project_anonymization.anonymizer.exceptions;

public class InvalidDictionaryException extends AnonymizerServiceException {
    public InvalidDictionaryException(String message) {
        super(message);
    }

    public InvalidDictionaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
