package com.project_anonymization.anonymizer.exceptions;

public class InvalidContentException extends AnonymizerServiceException {
    public InvalidContentException(String message) {
        super(message);
    }

    public InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
