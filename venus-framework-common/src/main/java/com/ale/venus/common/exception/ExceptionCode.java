package com.ale.venus.common.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum ExceptionCode {

    /**
     * 系统默认错误
     */
    DEFAULT_ERROR(1000, "系统错误"),

    /*---------------------- 登录 错误码301-399 -----------------------*/

    /**
     * 账号不存在
     */
    ACCOUNT_NOT_EXIST(301, "账号不存在"),

    /**
     * 账号已存在
     */
    ACCOUNT_IS_EXIST(302, "账号已存在"),

    /**
     * 账号已被删除
     */
    ACCOUNT_IS_DELETE(303, "账号已被删除"),

    /**
     * 账号已被封禁
     */
    ACCOUNT_IS_DISABLE(304, "账号已被封禁"),

    /**
     * 账号或秘密错误
     */
    ACCOUNT_OR_PASSWORD_ERROR(303, "账号或秘密错误"),

    /**
     * 登录方式不存在
     */
    LOGIN_TYPE_NOT_EXIST(311, "登录方式不存在"),

    /**
     * 两次密码不一致
     */
    PASSWORD_IS_INCONSISTENT(321, "两次密码不一致"),


    /*---------------------- 框架相关 错误码401-499 -----------------------*/

    /**
     * 没有权限
     */
    UNAUTHORIZED(401, "没有权限访问"),

    /**
     * 权限不足
     */
    ACCESS_DENIED(402, "权限不足"),

    /**
     * 认证信息不存在
     */
    AUTHENTICATION_NOT_EXIST(403, "认证信息不存在"),

    /**
     * token过期
     */
    AUTHORIZED_EXPIRE(410, "token已过期"),

    /**
     * token无效
     */
    AUTHORIZED_INVALID(411, "token无效"),

    /**
     * 登录状态已过期
     */
    LOGIN_EXPIRE(412, "登录已失效，请重新登录"),

    /**
     * 账号在其他地方登录
     */
    AUTHORIZED_BE_REPLACED(413, "账号在其他地方登录"),

    /**
     * 账号被踢下线
     */
    AU_KICK_OUT(414, "账号已被踢下线"),

    /**
     * 未通过参数验证
     */
    PARAMETER_VALIDATION_FAILED(421, "未通过参数验证"),

    /**
     * SRA签名错误
     */
    SRA_KEY_ERROR(441, "签名密钥错误，请重新获取"),

    /*---------------------- 业务相关 错误码501-599 -----------------------*/

    /**
     * 默认业务错误
     */
    DEFAULT_SERVICE_ERROR(500, "业务错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String msg;

    ExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
