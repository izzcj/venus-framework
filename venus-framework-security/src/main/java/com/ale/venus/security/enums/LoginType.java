package com.ale.venus.security.enums;

import com.ale.venus.common.enumeration.BaseEnum;

/**
 * 登录方式枚举
 *
 * @author Ale
 * @version 1.0.0
 */
public enum LoginType implements BaseEnum<String> {

    /**
     * 通过账号和密码登录
     */
    ACCOUNT_PASSWORD,

    /**
     * 通过账号和验证码登录
     */
    ACCOUNT_CAPTCHA,

    /**
     * 通过邮箱和密码登录
     */
    EMAIL_PASSWORD,

    /**
     * 通过邮箱和验证码登录
     */
    EMAIL_CAPTCHA;

    LoginType() {
        this.init();
    }
}
