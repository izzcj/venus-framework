package com.ale.venus.security.authorization.servlet;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 请求访问拒绝处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("访问被拒：{}", accessDeniedException.getMessage());
        ServletUtils.responseJson(
            response,
            HttpStatus.FORBIDDEN,
            JsonResult.fail(ExceptionCode.UNAUTHORIZED)
        );
    }
}
