package com.project_anonymization.dictionary.exceptions;

public class InvalidDictionaryException extends DictionaryServiceException {
    public InvalidDictionaryException(String message) {
        super(message);
    }

    public InvalidDictionaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
