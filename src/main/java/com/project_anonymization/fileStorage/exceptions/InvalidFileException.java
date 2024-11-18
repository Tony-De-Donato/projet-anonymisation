package com.project_anonymization.fileStorage.exceptions;

public class InvalidFileException extends FileStorageException {
    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
