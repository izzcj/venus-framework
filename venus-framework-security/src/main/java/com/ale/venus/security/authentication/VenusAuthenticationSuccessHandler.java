package com.ale.venus.security.authentication;

import cn.hutool.extra.spring.SpringUtil;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.utils.ServletUtils;
import com.ale.venus.security.contanst.SecurityConstants;
import com.ale.venus.security.context.TokenContext;
import com.ale.venus.security.event.VenusLoginSuccessEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 认证成功处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        log.info("{}: {} 登录成功!", authenticatedUser.getName(), authenticatedUser.getId());
        TokenContext tokenContext = (TokenContext) request.getAttribute(SecurityConstants.TOKEN_CONTEXT_KEY);
        if (tokenContext == null) {
            ServletUtils.responseJson(
                response,
                HttpStatus.UNAUTHORIZED,
                JsonResult.fail("登录失败：生成Token异常")
            );
        }

        ServletUtils.responseJson(
            response,
            HttpStatus.OK,
            JsonResult.success("登录成功", tokenContext)
        );
        SpringUtil.publishEvent(new VenusLoginSuccessEvent(this, authenticatedUser));
    }
}
