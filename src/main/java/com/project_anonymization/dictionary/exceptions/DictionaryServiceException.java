package com.project_anonymization.dictionary.exceptions;

public class DictionaryServiceException extends Exception {
    public DictionaryServiceException(String message) {
        super(message);
    }

    public DictionaryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

