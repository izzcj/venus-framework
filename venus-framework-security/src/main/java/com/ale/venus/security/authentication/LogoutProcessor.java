package com.ale.venus.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

/**
 * 登出处理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface LogoutProcessor {

    /**
     * 登出处理器
     *
     * @param request        请求对象
     * @param response       响应对象
     * @param authentication 认证对象
     */
    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

}
