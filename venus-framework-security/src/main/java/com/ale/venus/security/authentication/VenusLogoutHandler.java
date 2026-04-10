package com.ale.venus.security.authentication;

import com.ale.venus.common.security.ExpirationAwareToken;
import com.ale.venus.common.security.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * 退出登录处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class VenusLogoutHandler implements LogoutHandler {

    /**
     * Token管理器
     */
    private final TokenManager tokenManager;

    /**
     * 登出处理器
     */
    private final ObjectProvider<LogoutProcessor> logoutProcessors;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 过期Token
        if (authentication instanceof ExpirationAwareToken token) {
            this.tokenManager.expiresToken(token);
        }

        // 处理器处理登出操作
        for (LogoutProcessor logoutProcessor : this.logoutProcessors) {
            logoutProcessor.logout(request, response, authentication);
        }
    }
}
