package com.ale.venus.common.exception.handler;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.exception.ServiceException;
import com.ale.venus.common.exception.VenusException;
import com.ale.venus.common.spring.OrderedCorsFilter;
import com.ale.venus.common.utils.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * 全局异常捕获过滤器
 * 此阶段无法使用{@link org.springframework.web.bind.annotation.RestControllerAdvice}
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionCatchFilter extends OncePerRequestFilter implements OrderedFilter {

    /**
     * 默认排序
     */
    public static final int DEFAULT_ORDER = OrderedCorsFilter.DEFAULT_ORDER + 1000;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, new VenusHttpServletResponseWrapper(response));
        } catch (VenusException e) {
            this.handleVenusException(e, response);
        } catch (ServletException e) {
            Throwable cause = e.getRootCause();
            if (cause == null) {
                cause = e.getCause();
            }
            if (cause instanceof VenusException exception) {
                this.handleVenusException(exception, response);
            } else if (cause instanceof DataAccessException exception) {
                this.handleDataAccessException(exception, response);
            } else {
                this.handleUnexpectedException(cause != null ? cause : e, request, response);
            }
        } catch (Throwable e) {
            this.handleUnexpectedException(e, request, response);
        }
    }

    /**
     * 处理venus框架异常
     *
     * @param exception 异常
     * @param response  响应
     * @throws IOException IO异常
     */
    private void handleVenusException(VenusException exception, HttpServletResponse response) throws IOException {
        log.warn(
            "{}异常：{}",
            exception instanceof ServiceException ? "业务" : "Venus框架",
            exception.getMessage()
        );
        ServletUtils.responseJson(
            response,
            HttpStatus.OK,
            JsonResult.fail(exception.getCode(), exception.getMessage())
        );
    }

    /**
     * 处理数据库访问异常
     *
     * @param exception 异常
     * @param response  响应
     * @throws IOException IO异常
     */
    private void handleDataAccessException(DataAccessException exception, HttpServletResponse response) throws IOException {
        Throwable cause = exception.getCause();
        if (cause == null) {
            return;
        }

        log.warn("数据库异常：", cause);
        ServletUtils.responseJson(
            response,
            HttpStatus.INTERNAL_SERVER_ERROR,
            JsonResult.fail(ExceptionCode.DEFAULT_ERROR.getCode(), "数据库异常：{}", cause.getMessage())
        );
    }

    /**
     * 处理未预期异常
     *
     * @param e        异常
     * @param request  请求
     * @param response 响应
     * @throws IOException IO异常
     */
    private void handleUnexpectedException(Throwable e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.warn("请求：{}，出现未预期异常：{}", request.getRequestURI(), e.getMessage(), e);
        ServletUtils.responseJson(
            response,
            HttpStatus.INTERNAL_SERVER_ERROR,
            JsonResult.fail(ExceptionCode.DEFAULT_ERROR.getCode(), "服务器异常：{}", e.getMessage())
        );
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
