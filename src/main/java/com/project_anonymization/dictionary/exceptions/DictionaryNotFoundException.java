package com.project_anonymization.dictionary.exceptions;

public class DictionaryNotFoundException extends DictionaryServiceException {
    public DictionaryNotFoundException(String message) {
        super(message);
    }

    public DictionaryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}