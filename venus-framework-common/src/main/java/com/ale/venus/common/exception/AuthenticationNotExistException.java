package com.ale.venus.common.exception;

/**
 * 认证信息不存在异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class AuthenticationNotExistException extends VenusException {

    public AuthenticationNotExistException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public AuthenticationNotExistException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    public AuthenticationNotExistException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public AuthenticationNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationNotExistException(int code, String message) {
        super(code, message);
    }

    public AuthenticationNotExistException(int code, String message, Object... args) {
        super(code, message, args);
    }

    public AuthenticationNotExistException(String message) {
        super(message);
    }

    public AuthenticationNotExistException(String message, Object... args) {
        super(message, args);
    }

    public AuthenticationNotExistException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
