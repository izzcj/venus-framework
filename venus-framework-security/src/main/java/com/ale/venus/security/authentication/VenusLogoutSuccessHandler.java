package com.ale.venus.security.authentication;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * 退出登录成功处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * 退出登录成功处理
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication 认证信息对象
     * @throws IOException IO异常
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser) {
            log.info("{}: {} 退出登录成功!", authenticatedUser.getName(), authenticatedUser.getId());
        }

        ServletUtils.responseJson(
            response,
            HttpStatus.OK,
            JsonResult.success("退出登录成功")
        );
    }
}
