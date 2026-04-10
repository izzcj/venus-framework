package com.ale.venus.security.authorization.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

/**
 * 授权管理器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class VenusAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    /**
     * 认证授权管理器
     */
    private final AuthenticatedAuthorizationManager<RequestAuthorizationContext> authenticatedAuthorizationManager = new AuthenticatedAuthorizationManager<>();

    /**
     * 权限检查器
     */
    private final ObjectProvider<RequestPermissionChecker> permissionCheckers;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationDecision authorizationDecision = this.authenticatedAuthorizationManager.check(authentication, object);
        // 未认证直接返回
        if (!authorizationDecision.isGranted()) {
            return authorizationDecision;
        }

        // 权限校验
        HttpServletRequest request = object.getRequest();
        boolean granted = this.permissionCheckers.orderedStream()
            .anyMatch(
                requestPermissionChecker -> requestPermissionChecker.check(
                    HttpMethod.valueOf(request.getMethod()),
                    request.getRequestURI()
                )
            );

        return new AuthorizationDecision(granted);
    }

}
