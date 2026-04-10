package com.ale.venus.common.exception;

/**
 * 不合适的响应码异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class BadResultCodeException extends VenusException {

    public BadResultCodeException(int code, String message) {
        super(code, message);
    }

    public BadResultCodeException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public BadResultCodeException(String message) {
        super(message);
    }

    public BadResultCodeException(String message, Object... args) {
        super(message, args);
    }

    public BadResultCodeException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BadResultCodeException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public BadResultCodeException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public BadResultCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
