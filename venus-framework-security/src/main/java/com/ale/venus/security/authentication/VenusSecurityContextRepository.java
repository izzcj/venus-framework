package com.ale.venus.security.authentication;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.security.ExpirationAwareToken;
import com.ale.venus.common.security.TokenManager;
import com.ale.venus.security.contanst.SecurityConstants;
import com.ale.venus.security.context.TokenContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * 安全上下文仓库
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class VenusSecurityContextRepository implements SecurityContextRepository {
    /**
     * 访问Token参数名称
     */
    private static final String ACCESS_TOKEN_PARAMETER_NAME = "accessToken";

    /**
     * Token管理器
     */
    private final TokenManager tokenManager;

    /**
     * 是否刷新Token过期时长
     */
    private final boolean refreshTokenExpiration;


    @Deprecated
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return null;
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new SupplierDeferredSecurityContext(
            () -> {
                var token = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (StrUtil.isBlank(token)) {
                    token = request.getParameter(ACCESS_TOKEN_PARAMETER_NAME);
                }

                String accessToken = this.tokenManager.extractAccessToken(token);
                if (accessToken == null) {
                    return null;
                }

                Authentication authentication = this.tokenManager.extractAuthentication(accessToken);
                if (authentication == null) {
                    return null;
                }

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);

                if (this.refreshTokenExpiration && authentication instanceof ExpirationAwareToken expirationAwareToken) {
                    // 刷新认证Context
                    this.tokenManager.refreshAccessTokenExpiration(expirationAwareToken);
                }

                return securityContext;
            },
            SecurityContextHolder.getContextHolderStrategy()
        );
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if (context.getAuthentication() == null) {
            return;
        }
        String accessToken = this.tokenManager.generateAccessToken(context.getAuthentication());
        request.setAttribute(
            SecurityConstants.TOKEN_CONTEXT_KEY,
            new TokenContext(accessToken, null, this.tokenManager.getAccessTokenExpiration().toSeconds())
        );
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String accessToken = this.tokenManager.extractAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        return accessToken != null;
    }
}
