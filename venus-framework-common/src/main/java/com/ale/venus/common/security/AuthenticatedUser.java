package com.ale.venus.common.security;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 认证用户
 *
 * @author Ale
 * @version 1.0.0
 */
public interface AuthenticatedUser extends UserDetails, CredentialsContainer {

    @Override
    default String getUsername() {
        return this.getAccount();
    }

    /**
     * 获取用户账号
     *
     * @return 用户账号
     */
    String getAccount();

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    Long getId();

    /**
     * 获取用户姓名
     *
     * @return 用户姓名
     */
    String getName();
}
