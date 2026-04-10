package com.ale.venus.security.contanst;

/**
 * Security常量
 *
 * @author Ale
 * @version 1.0.0
 */
public final class SecurityConstants {

    /**
     * BearerToken前缀
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Token上下文存储Key
     */
    public static final String TOKEN_CONTEXT_KEY = "venusSecurityTokenContext";

    /**
     * 角色前缀
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 登录URI
     */
    public static final String LOGIN_URI = "/auth/login";

    /**
     * 登出URI
     */
    public static final String LOGOUT_URI = "/auth/logout";

    private SecurityConstants() {
    }
}
