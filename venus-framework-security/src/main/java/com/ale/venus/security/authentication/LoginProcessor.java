package com.ale.venus.security.authentication;

import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.support.Supportable;
import com.ale.venus.security.enums.LoginType;

import java.util.Map;

/**
 * 登录处理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface LoginProcessor extends Supportable<LoginType> {

    /**
     * 登录
     *
     * @param parameters 登录参数
     * @return 认证用户
     */
    AuthenticatedUser login(Map<String, Object> parameters);
}
