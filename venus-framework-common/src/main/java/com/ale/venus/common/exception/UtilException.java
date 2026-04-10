package com.ale.venus.common.exception;

/**
 * 工具类异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class UtilException extends VenusException {

    public UtilException(int code, String message) {
        super(code, message);
    }

    public UtilException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Object... args) {
        super(message, args);
    }

    public UtilException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public UtilException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public UtilException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }
}
