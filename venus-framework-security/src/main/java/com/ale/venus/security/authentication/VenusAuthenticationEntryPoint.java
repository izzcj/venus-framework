package com.ale.venus.security.authentication;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.utils.ServletUtils;
import com.ale.venus.security.exception.VenusSecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 认证入口
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Throwable cause = authException.getCause();
        if (cause instanceof VenusSecurityException venusSecurityException) {
            ServletUtils.responseJson(
                response,
                HttpStatus.UNAUTHORIZED,
                JsonResult.fail(venusSecurityException.getCode(), venusSecurityException.getMessage())
            );
        } else {
            log.warn("需要认证：{}", authException.getMessage());
            ServletUtils.responseJson(
                response,
                HttpStatus.UNAUTHORIZED,
                JsonResult.fail(ExceptionCode.UNAUTHORIZED)
            );
        }
    }
}
