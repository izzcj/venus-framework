package com.ale.venus.workflow.exception;

import cn.hutool.core.util.StrUtil;

/**
 * 流程引擎异常
 *
 * @author Ale
 * @version 1.0.0
 */
public class FlowException extends RuntimeException {

    public FlowException() {
    }

    public FlowException(String msg, Throwable cause) {
        super(msg);
        super.initCause(cause);
    }

    public FlowException(String msg) {
        super(msg);
    }

    public FlowException(String msg, Object... args) {
        super(StrUtil.format(msg, args));
    }

    public FlowException(Throwable cause) {
        super.initCause(cause);
    }

}
