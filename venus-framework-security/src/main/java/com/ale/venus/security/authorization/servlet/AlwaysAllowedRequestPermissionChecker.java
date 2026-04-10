package com.ale.venus.security.authorization.servlet;

import org.springframework.http.HttpMethod;

/**
 * 不进行权限检查
 *
 * @author Ale
 * @version 1.0.0
 */
public class AlwaysAllowedRequestPermissionChecker implements RequestPermissionChecker {

    @Override
    public boolean check(HttpMethod method, String requestUri) {
        return true;
    }
}
