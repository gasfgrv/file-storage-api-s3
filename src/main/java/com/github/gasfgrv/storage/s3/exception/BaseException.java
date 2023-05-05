package com.github.gasfgrv.storage.s3.exception;

import java.util.Objects;

public abstract class BaseException extends RuntimeException {
    protected BaseException(Exception exception) {
        super(getCauseMessage(exception), exception);
    }

    private static String getCauseMessage(Exception exception) {
        return Objects.isNull(exception.getCause())
                ? "Sem causa atribuida"
                : exception.getCause().getMessage();
    }
}
