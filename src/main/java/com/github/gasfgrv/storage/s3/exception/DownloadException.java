package com.github.gasfgrv.storage.s3.exception;

public class DownloadException extends RuntimeException {
    public DownloadException(Exception exception) {
        super(exception.getCause().getMessage(), exception);
    }
}
