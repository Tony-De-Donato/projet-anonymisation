package com.netceler.project_anonymization.anonymizer.exceptions;

public class AnonymizerServiceException extends Exception {
    public AnonymizerServiceException(String message) {
        super(message);
    }

    public AnonymizerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
