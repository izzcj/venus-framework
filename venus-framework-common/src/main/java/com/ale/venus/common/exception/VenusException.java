package com.ale.venus.common.exception;

import cn.hutool.core.text.StrFormatter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 异常基类
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class VenusException extends RuntimeException {

    /**
     * 异常响应码
     */
    private final int code;

    public VenusException() {
        super(ExceptionCode.DEFAULT_SERVICE_ERROR.getMsg());
        this.code = ExceptionCode.DEFAULT_ERROR.getCode();
    }

    public VenusException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMsg());
        this.code = exceptionCode.getCode();
    }

    public VenusException(int code, String message) {
        super(message);
        this.code = code;
    }

    public VenusException(int code, String message, Object... args) {
        super(StrFormatter.format(message, args));
        this.code = code;
    }

    public VenusException(String message) {
        super(message);
        this.code = ExceptionCode.DEFAULT_ERROR.getCode();
    }

    public VenusException(String message, Object... args) {
        super(StrFormatter.format(message, args));
        this.code = ExceptionCode.DEFAULT_ERROR.getCode();
    }

    public VenusException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public VenusException(int code, String message, Throwable cause, Object... args) {
        super(StrFormatter.format(message, args), cause);
        this.code = code;
    }

    public VenusException(String message, Throwable cause) {
        super(message, cause);
        this.code = ExceptionCode.DEFAULT_ERROR.getCode();
    }

    public VenusException(String message, Throwable cause, Object... args) {
        super(StrFormatter.format(message, args), cause);
        this.code = ExceptionCode.DEFAULT_ERROR.getCode();
    }
}
