package com.ale.venus.security.authentication;

import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.security.enums.LoginType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 聚合认证token
 *
 * @author Ale
 * @version 1.0.0
 */
public class CompositeAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 登录类型
     */
    @Getter
    private LoginType loginType;

    /**
     * 登录参数
     */
    @Getter
    private Map<String, Object> parameters;

    /**
     * 已认证用户
     */
    private AuthenticatedUser authenticatedUser;

    private CompositeAuthenticationToken(LoginType loginType, Map<String, Object> parameters) {
        super(null);
        this.loginType = loginType;
        this.parameters = parameters;
        super.setAuthenticated(false);
    }

    public CompositeAuthenticationToken(AuthenticatedUser user) {
        super(user.getAuthorities());
        this.authenticatedUser = user;
        super.setAuthenticated(true);
    }

    /**
     * 构建未认证的token
     *
     * @param loginType  登录类型
     * @param parameters 认证参数
     * @return 聚合认证token
     */
    public static CompositeAuthenticationToken unauthenticated(LoginType loginType, Map<String, Object> parameters) {
        return new CompositeAuthenticationToken(loginType, parameters);
    }

    /**
     * 构建已认证的token
     *
     * @param user 已认证用户
     * @return 聚合认证token
     */
    public static CompositeAuthenticationToken authenticated(AuthenticatedUser user) {
        return new CompositeAuthenticationToken(user);
    }

    @Override
    public Object getCredentials() {
        return this.parameters;
    }

    @Override
    public Object getPrincipal() {
        return this.authenticatedUser;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
            "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }
}
