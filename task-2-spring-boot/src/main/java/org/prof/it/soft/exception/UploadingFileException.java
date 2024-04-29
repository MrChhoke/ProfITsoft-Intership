package org.prof.it.soft.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class represents a custom exception for file uploading errors.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadingFileException extends RuntimeException {

    /**
     * Constructs a new UploadingFileException with no detail message.
     */
    public UploadingFileException() {
        super();
    }

    /**
     * Constructs a new UploadingFileException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
     */
    public UploadingFileException(String message) {
        super(message);
    }

    /**
     * Constructs a new UploadingFileException with the specified detail message and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *             (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UploadingFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UploadingFileException with the specified cause and a detail
     * message of (cause==null ? null : cause.toString()) (which typically contains the class
     * and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     *             (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UploadingFileException(Throwable cause) {
        super(cause);
    }
}