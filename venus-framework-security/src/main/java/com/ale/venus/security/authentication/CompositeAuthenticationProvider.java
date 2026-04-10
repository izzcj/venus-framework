package com.ale.venus.security.authentication;

import com.ale.venus.common.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * 聚合认证提供器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class CompositeAuthenticationProvider implements AuthenticationProvider {

    /**
     * 默认认证信息检查器
     */
    private final UserDetailsChecker authenticationChecker = new DefaultPreAuthenticationChecker();

    /**
     * 登录处理器
     */
    private final ObjectProvider<LoginProcessor> loginProcessors;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CompositeAuthenticationToken authenticationToken = (CompositeAuthenticationToken) authentication;
        AuthenticatedUser authenticatedUser = null;
        for (LoginProcessor loginProcessor : this.loginProcessors) {
            if (loginProcessor.supports(authenticationToken.getLoginType())) {
                authenticatedUser = loginProcessor.login(authenticationToken.getParameters());
            }
        }

        if (authenticatedUser == null) {
            throw new AuthenticationServiceException("不支持的认证类型：" + authenticationToken.getLoginType());
        }

        this.authenticationChecker.check(authenticatedUser);
        return this.createSuccessAuthentication(authentication, authenticatedUser);
    }

    /**
     * Creates a successful {@link Authentication} object.
     * <p>
     * Protected so subclasses can override.
     * </p>
     * <p>
     * Subclasses will usually store the original credentials the user supplied (not
     * salted or encoded passwords) in the returned <code>Authentication</code> object.
     * </p>
     *
     * @param authentication that was presented to the provider for validation
     * @param user that was loaded by the implementation
     * @return the successful authentication token
     */
    protected Authentication createSuccessAuthentication(Authentication authentication, AuthenticatedUser user) {
        CompositeAuthenticationToken result = CompositeAuthenticationToken.authenticated(user);
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CompositeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
