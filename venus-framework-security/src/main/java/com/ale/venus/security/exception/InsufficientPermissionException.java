package com.ale.venus.security.exception;

import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.exception.VenusException;

/**
 * 权限不足异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class InsufficientPermissionException extends VenusException {

    public InsufficientPermissionException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public InsufficientPermissionException(int code, String message) {
        super(code, message);
    }

    public InsufficientPermissionException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public InsufficientPermissionException(String message) {
        super(message);
    }

    public InsufficientPermissionException(String message, Object... args) {
        super(message, args);
    }

    public InsufficientPermissionException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public InsufficientPermissionException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public InsufficientPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientPermissionException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }
}
