package com.project_anonymization.fileStorage.exceptions;

public class FileNotFoundException extends FileStorageException {
    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
