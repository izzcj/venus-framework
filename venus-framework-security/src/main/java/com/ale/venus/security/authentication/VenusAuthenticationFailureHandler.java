package com.ale.venus.security.authentication;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * 认证失败处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("登录失败：{}", exception.getMessage());
        ServletUtils.responseJson(
            response,
            HttpStatus.OK,
            JsonResult.fail("登录失败：{}", exception.getMessage())
        );
    }
}
