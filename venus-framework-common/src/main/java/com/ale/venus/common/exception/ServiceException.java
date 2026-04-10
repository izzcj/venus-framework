package com.ale.venus.common.exception;

/**
 * 业务异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class ServiceException extends VenusException {

    public ServiceException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public ServiceException(int code, String message) {
        super(code, message);
    }

    public ServiceException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public ServiceException(String message, Object... args) {
        super(message, args);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public ServiceException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }
}
