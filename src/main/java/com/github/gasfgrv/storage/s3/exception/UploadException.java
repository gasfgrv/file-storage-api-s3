package com.github.gasfgrv.storage.s3.exception;

public class UploadException extends RuntimeException {

    public UploadException(Exception exception) {
        super(exception.getCause().getMessage(), exception);
    }
}
