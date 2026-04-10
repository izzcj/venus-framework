package com.ale.venus.security.exception;

import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.exception.VenusException;

/**
 * VenusSecurity异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class VenusSecurityException extends VenusException {

    public VenusSecurityException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public VenusSecurityException(int code, String message) {
        super(code, message);
    }

    public VenusSecurityException(String message) {
        super(message);
    }

    public VenusSecurityException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public VenusSecurityException(String message, Object... args) {
        super(message, args);
    }

    public VenusSecurityException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public VenusSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public VenusSecurityException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public VenusSecurityException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }
}
